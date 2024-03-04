package app.habit.service.gpt;

import app.habit.domain.Answer;
import app.habit.domain.EvaluationCoach;
import app.habit.domain.EvaluationPromptFactory;
import app.habit.domain.FeedbackCoach;
import app.habit.domain.FeedbackModule;
import app.habit.domain.FeedbackPromptFactory;
import app.habit.domain.FeedbackSession;
import app.habit.domain.HabitAssessmentManager;
import app.habit.domain.HabitAssessmentManagerFactory;
import app.habit.domain.HabitFormingPhase;
import app.habit.domain.HabitFormingPhaseType;
import app.habit.domain.PhaseCoach;
import app.habit.domain.PreQuestionCoach;
import app.habit.domain.PromptFactory;
import app.habit.domain.Subject;
import app.habit.dto.HabitPreQuestionRs;
import app.habit.dto.PhaseAnswerRq;
import app.habit.dto.PhaseEvaluationAnswerRq;
import app.habit.dto.PhaseEvaluationRq;
import app.habit.dto.PhaseEvaluationRs;
import app.habit.dto.PhaseFeedbackRq;
import app.habit.dto.PhaseFeedbackRs;
import app.habit.dto.UserHabitPreQuestionRq;
import app.habit.dto.UserHabitPreQuestionRs;
import app.habit.repository.FeedbackModuleRepository;
import app.habit.repository.HabitFormingPhaseRepository;
import app.habit.service.gpt.request.RequestPrompt;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    private static final String url = "https://api.openai.com/v1/chat/completions";

    private final PreQuestionCoach preQuestionCoach;
    private final PromptFactory promptFactory;
    private final HabitAssessmentManagerFactory habitAssessmentManagerFactory;

    private final HabitFormingPhaseRepository habitFormingPhaseRepository;
    private final EvaluationPromptFactory evaluationPromptFactory;
    private final EvaluationCoach evaluationCoach;

    // 3 api
    private final PhaseCoach phaseCoach;

    // 4 api
    private final FeedbackModuleRepository feedbackModuleRepository;
    private final FeedbackPromptFactory feedbackPromptFactory;
    private final FeedbackCoach feedbackCoach;

    @Transactional
    public List<HabitPreQuestionRs> getHabitPreQuestions(Long phaseId, String type, String prompt) {
        RequestPrompt requestPrompt = promptFactory.createRequestBody(type, prompt);
        List<HabitPreQuestionRs> content = preQuestionCoach.advice(requestPrompt, url);
        HabitAssessmentManager habitAssessmentManager = habitAssessmentManagerFactory.createHabitAssessmentManager(
                content);
        HabitFormingPhase findHabitFormingPhase = habitFormingPhaseRepository.findById(phaseId)
                .orElseThrow(RuntimeException::new);
        findHabitFormingPhase.addHabitAssessmentManager(habitAssessmentManager);
        return content;
    }

    @Transactional
    public PhaseEvaluationRs evaluateHabitPhase(PhaseEvaluationRq rq) {
        HabitAssessmentManager habitAssessmentManager = findHabitAssessmentManager(rq);
        List<Subject> subjects = habitAssessmentManager.getSubjects();

        // save answer
        List<Answer> answers = saveAnswerInSubject(rq, subjects);

        // create prompt
        RequestPrompt requestPrompt = evaluationPromptFactory.create(answers, subjects);

        // request advice
        PhaseEvaluationRs advice = evaluationCoach.advice(requestPrompt, url);

        // save advice
        saveHabitAssessment(advice, habitAssessmentManager);

        return advice;
    }

    private HabitAssessmentManager findHabitAssessmentManager(PhaseEvaluationRq rq) {
        long habitFormingPhaseId = rq.getHabitFormingPhaseId();
        HabitFormingPhase habitFormingPhase = habitFormingPhaseRepository.findById(habitFormingPhaseId).orElseThrow();
        return habitFormingPhase.getHabitAssessmentManager();
    }

    private List<Answer> saveAnswerInSubject(PhaseEvaluationRq rq, List<Subject> subjects) {
        List<PhaseEvaluationAnswerRq> rqs = rq.getAnswers();
        List<Answer> answers = rqs.stream().map(phaseEvaluationAnswerRq -> new Answer(phaseEvaluationAnswerRq.getKey(),
                phaseEvaluationAnswerRq.getUserAnswer())).collect(Collectors.toList());

        for (int i = 0; i < subjects.size(); i++) {
            subjects.get(i).addAnswer(answers.get(i));
        }
        return answers;
    }

    private void saveHabitAssessment(PhaseEvaluationRs advice, HabitAssessmentManager habitAssessmentManager) {
        HabitFormingPhaseType phaseType = HabitFormingPhaseType.findType(advice.getPhaseType());
        String phaseDescription = advice.getPhaseDescription();
        habitAssessmentManager.evaluate(phaseType, phaseDescription);
    }

    // 사용자의 습관 형성 단계에 따른 사전 질문 가져오기 api
    @Transactional
    public List<UserHabitPreQuestionRs> getSpecificPhasePreQuestions(UserHabitPreQuestionRq rq, String type) {
        HabitFormingPhaseType userHabitPhaseType = rq.getPhaseType();
        RequestPrompt requestPrompt = promptFactory.createRequestBody(type, "");
        List<UserHabitPreQuestionRs> advice = phaseCoach.advice(requestPrompt, url);

        // find
        HabitFormingPhase userHabitFormingPhase = habitFormingPhaseRepository.findById(rq.getHabitFormingPhaseId())
                .orElseThrow();

        // save 로직
        List<FeedbackModule> feedbackModules = createFeedbackModules(advice);

        // add modules to phase (feedbackModule을 더해줌)
        userHabitFormingPhase.addFeedbackModules(feedbackModules);

        // return response to user
        return advice;
    }

    private List<FeedbackModule> createFeedbackModules(List<UserHabitPreQuestionRs> advice) {
        return advice.stream().map(UserHabitPreQuestionRs::toFeedbackModule)
                .collect(Collectors.toList());
    }

    // 답변 제출 및 피드백 받기 api
    @Transactional
    public PhaseFeedbackRs getFeedback(PhaseFeedbackRq rq) {
        FeedbackModule feedbackModule = feedbackModuleRepository.findById(rq.getFeedbackModuleId()).orElseThrow();

        // gpt api 호출
        RequestPrompt requestPrompt = feedbackPromptFactory.create();
        PhaseFeedbackRs advice = feedbackCoach.advice(requestPrompt, url);

        // gpt response save +  user answer save
        String feedbackSubject = advice.getFeedbackSubject();
        String feedback = advice.getFeedback();

        Map<String, FeedbackSession> feedbackSessionMap = new HashMap<>();
        for (FeedbackSession feedbackSession : feedbackModule.getFeedbackSessions()) {
            String sessionKey = feedbackSession.getSessionKey();
            feedbackSessionMap.put(sessionKey, feedbackSession);
        }

        FeedbackSession sameFeedbackSession = feedbackModule.getFeedbackSessions().stream()
                .filter(session -> session.isSameSubject(feedbackSubject))
                .findFirst().orElseThrow();

        for (PhaseAnswerRq phaseAnswer : rq.getPhaseAnswers()) {
            String key = phaseAnswer.getKey();
            String userAnswer = phaseAnswer.getUserAnswer();

            FeedbackSession feedbackSession = feedbackSessionMap.get(key);
            feedbackSession.addAnswer(userAnswer);
            feedbackSession.addFeedback(feedback);
        }

        // return
        return advice;
    }

}
