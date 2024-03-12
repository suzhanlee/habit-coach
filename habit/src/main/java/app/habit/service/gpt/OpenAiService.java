package app.habit.service.gpt;

import app.habit.domain.Answer;
import app.habit.service.gpt.coach.EvaluationGptCoach;
import app.habit.domain.factory.EvaluationPromptFactory;
import app.habit.service.gpt.coach.FeedbackGptCoach;
import app.habit.domain.FeedbackModule;
import app.habit.domain.factory.FeedbackPromptFactory;
import app.habit.domain.FeedbackSession;
import app.habit.domain.HabitAssessmentManager;
import app.habit.domain.HabitFormingPhase;
import app.habit.service.gpt.coach.PhaseGptCoach;
import app.habit.service.gpt.coach.PreQuestionGptCoach;
import app.habit.domain.factory.PromptFactory;
import app.habit.domain.Question;
import app.habit.domain.factory.SpecificPhasePreQuestionPromptFactory;
import app.habit.domain.Subject;
import app.habit.dto.HabitPreQuestionRs;
import app.habit.dto.PhaseEvaluationRq;
import app.habit.dto.PhaseEvaluationRs;
import app.habit.dto.PhaseFeedbackRq;
import app.habit.dto.PhaseFeedbackRs;
import app.habit.dto.QuestionRs;
import app.habit.dto.UserHabitPreQuestionRq;
import app.habit.dto.UserHabitPreQuestionRs;
import app.habit.repository.FeedbackModuleRepository;
import app.habit.repository.HabitFormingPhaseRepository;
import app.habit.service.gpt.request.RequestPrompt;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OpenAiService {

    private static final String url = "https://api.openai.com/v1/chat/completions";

    private final PreQuestionGptCoach preQuestionGptCoach;
    private final PromptFactory promptFactory;

    private final HabitFormingPhaseRepository habitFormingPhaseRepository;
    private final EvaluationPromptFactory evaluationPromptFactory;
    private final EvaluationGptCoach evaluationGptCoach;

    private final SpecificPhasePreQuestionPromptFactory specificPhasePreQuestionPromptFactory;
    private final PhaseGptCoach phaseGptCoach;

    private final FeedbackModuleRepository feedbackModuleRepository;
    private final FeedbackPromptFactory feedbackPromptFactory;
    private final FeedbackGptCoach feedbackGptCoach;

    public List<HabitPreQuestionRs> getHabitPreQuestions(Long phaseId, String type, String prompt) {
        // request -> gpt
        RequestPrompt requestPrompt = promptFactory.create(type, prompt);
        List<HabitPreQuestionRs> preQuestionRs = preQuestionGptCoach.requestPreQuestions(requestPrompt, url);

        // find
        HabitFormingPhase findHabitFormingPhase = habitFormingPhaseRepository.findById(phaseId)
                .orElseThrow(RuntimeException::new);

        // save
        HabitAssessmentManager habitAssessmentManager = new HabitAssessmentManager(preQuestionRs.stream()
                .map(rs -> {
                    String key = rs.getKey();
                    String subject = rs.getSubject();
                    QuestionRs questionRs = rs.getQuestionRs();
                    return new Subject(key, subject,
                            new Question(questionRs.getQuestionKey(), questionRs.getQuestion()));
                })
                .toList());
        findHabitFormingPhase.addHabitAssessmentManager(habitAssessmentManager);

        // return
        return preQuestionRs;
    }

    public PhaseEvaluationRs evaluateHabitPhase(PhaseEvaluationRq rq) {
        // find habitFormingPhase
        HabitFormingPhase habitFormingPhase = habitFormingPhaseRepository.findById(rq.getHabitFormingPhaseId())
                .orElseThrow();

        // save answers according to subject
        habitFormingPhase.addAnswers(rq.getAnswers().stream()
                .map(phaseEvaluationAnswerRq -> new Answer(
                        phaseEvaluationAnswerRq.getKey(),
                        phaseEvaluationAnswerRq.getUserAnswer())
                ).toList());

        // request -> gpt
        RequestPrompt requestPrompt = evaluationPromptFactory.create(habitFormingPhase);
        PhaseEvaluationRs evaluationRs = evaluationGptCoach.requestEvaluation(requestPrompt, url);

        // save evaluation result
        String phaseType = evaluationRs.getPhaseType();
        String phaseDescription = evaluationRs.getPhaseDescription();
        habitFormingPhase.addEvaluationResult(phaseType, phaseDescription);

        return evaluationRs;
    }

    public List<UserHabitPreQuestionRs> getSpecificPhasePreQuestions(UserHabitPreQuestionRq rq) {
        // request -> gpt
        RequestPrompt requestPrompt = specificPhasePreQuestionPromptFactory.create(rq.getHabitFormingPhaseType());
        List<UserHabitPreQuestionRs> userHabitPreQuestionRs = phaseGptCoach.requestUserHabitPreQuestions(requestPrompt, url);

        // save 로직
        List<FeedbackModule> feedbackModules = userHabitPreQuestionRs.stream()
                .map(rs -> {
                    FeedbackModule feedbackModule = new FeedbackModule(rs.getKey(), rs.getSubject());
                    rs.getQuestions()
                            .forEach(question -> feedbackModule.addSession(
                                    new FeedbackSession(question.getQuestionKey(), question.getQuestion()))
                            );
                    return feedbackModule;
                })
                .toList();

        // find
        HabitFormingPhase userHabitFormingPhase = habitFormingPhaseRepository.findById(rq.getHabitFormingPhaseId())
                .orElseThrow();

        // add modules to phase (feedbackModule을 더해줌)
        userHabitFormingPhase.addFeedbackModules(feedbackModules);

        // return response to user
        return userHabitPreQuestionRs;
    }

    public PhaseFeedbackRs getFeedbackAboutSpecificSubject(PhaseFeedbackRq rq) {
        FeedbackModule feedbackModule = feedbackModuleRepository.findById(rq.getFeedbackModuleId())
                .orElseThrow();

        // save answer
        rq.getPhaseAnswers()
                .forEach(phaseAnswer -> feedbackModule.addAnswer(phaseAnswer.getKey(), phaseAnswer.getUserAnswer()));

        // request -> gpt
        RequestPrompt requestPrompt = feedbackPromptFactory.create(feedbackModule, rq.getHabitFormingPhaseType());
        PhaseFeedbackRs phaseFeedbackRs = feedbackGptCoach.requestPhaseFeedback(requestPrompt, url);

        // save subject & feedback to feedbackModule
        feedbackModule.addSubject(phaseFeedbackRs.getFeedbackSubject());
        feedbackModule.addFeedback(phaseFeedbackRs.getFeedback());

        return phaseFeedbackRs;
    }
}
