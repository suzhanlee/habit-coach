package app.habit.service.gpt;

import app.habit.domain.Answer;
import app.habit.domain.EvaluationCoach;
import app.habit.domain.EvaluationPromptFactory;
import app.habit.domain.FeedbackCoach;
import app.habit.domain.FeedbackModule;
import app.habit.domain.FeedbackPromptFactory;
import app.habit.domain.HabitAssessmentManager;
import app.habit.domain.HabitAssessmentManagerFactory;
import app.habit.domain.HabitFormingPhase;
import app.habit.domain.HabitFormingPhaseType;
import app.habit.domain.PhaseCoach;
import app.habit.domain.PreQuestionCoach;
import app.habit.domain.PromptFactory;
import app.habit.domain.SpecificPhasePreQuestionPromptFactory;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OpenAiService {

    private static final String url = "https://api.openai.com/v1/chat/completions";

    private final PreQuestionCoach preQuestionCoach;
    private final PromptFactory promptFactory;
    private final HabitAssessmentManagerFactory habitAssessmentManagerFactory;

    private final HabitFormingPhaseRepository habitFormingPhaseRepository;
    private final EvaluationPromptFactory evaluationPromptFactory;
    private final EvaluationCoach evaluationCoach;

    private final SpecificPhasePreQuestionPromptFactory specificPhasePreQuestionPromptFactory;
    private final PhaseCoach phaseCoach;

    private final FeedbackModuleRepository feedbackModuleRepository;
    private final FeedbackPromptFactory feedbackPromptFactory;
    private final FeedbackCoach feedbackCoach;

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

    public PhaseEvaluationRs evaluateHabitPhase(PhaseEvaluationRq rq) {
        // find habitFormingPhase
        long habitFormingPhaseId = rq.getHabitFormingPhaseId();
        HabitFormingPhase habitFormingPhase = habitFormingPhaseRepository.findById(habitFormingPhaseId).orElseThrow();

        // save answers according to subject
        List<Answer> answers = createAnswers(rq);
        habitFormingPhase.addAnswers(answers);

        // create prompt
        RequestPrompt requestPrompt = evaluationPromptFactory.create(habitFormingPhase);

        // request advice
        PhaseEvaluationRs advice = evaluationCoach.advice(requestPrompt, url);

        // save advice
        saveHabitAssessment(advice, habitFormingPhase);

        return advice;
    }

    private List<Answer> createAnswers(PhaseEvaluationRq rq) {
        List<PhaseEvaluationAnswerRq> rqs = rq.getAnswers();
        return rqs.stream().map(phaseEvaluationAnswerRq -> new Answer(phaseEvaluationAnswerRq.getKey(),
                phaseEvaluationAnswerRq.getUserAnswer())).collect(Collectors.toList());
    }

    private void saveHabitAssessment(PhaseEvaluationRs advice, HabitFormingPhase habitFormingPhase) {
        HabitFormingPhaseType phaseType = HabitFormingPhaseType.findType(advice.getPhaseType());
        String phaseDescription = advice.getPhaseDescription();
        habitFormingPhase.getHabitAssessmentManager().assess(phaseType, phaseDescription);
    }

    public List<UserHabitPreQuestionRs> getSpecificPhasePreQuestions(UserHabitPreQuestionRq rq) {
        RequestPrompt requestPrompt = specificPhasePreQuestionPromptFactory.create(rq.getHabitFormingPhaseType());
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
        return advice.stream()
                .map(UserHabitPreQuestionRs::toFeedbackModule)
                .collect(Collectors.toList());
    }

    public PhaseFeedbackRs getFeedbackAboutSpecificSubject(PhaseFeedbackRq rq) {
        FeedbackModule feedbackModule = feedbackModuleRepository.findById(rq.getFeedbackModuleId()).orElseThrow();

        // save answer
        List<PhaseAnswerRq> phaseAnswers = rq.getPhaseAnswers();

        for (PhaseAnswerRq phaseAnswer : phaseAnswers) {
            feedbackModule.addAnswer(phaseAnswer.getKey(), phaseAnswer.getUserAnswer());
        }

        // gpt api 호출
        RequestPrompt requestPrompt = feedbackPromptFactory.create(feedbackModule, rq.getHabitFormingPhaseType());
        PhaseFeedbackRs advice = feedbackCoach.advice(requestPrompt, url);

        // save subject & feedback to feedbackModule
        feedbackModule.addSubject(advice.getFeedbackSubject());
        feedbackModule.addFeedback(advice.getFeedback());

        return advice;
    }
}
