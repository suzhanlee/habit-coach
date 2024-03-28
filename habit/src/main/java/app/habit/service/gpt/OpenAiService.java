package app.habit.service.gpt;

import app.habit.domain.FeedbackModule;
import app.habit.domain.FeedbackSession;
import app.habit.domain.Habit;
import app.habit.domain.HabitAssessmentManager;
import app.habit.domain.HabitFormingPhase;
import app.habit.domain.factory.EvaluationPromptFactory;
import app.habit.domain.factory.FeedbackPromptFactory;
import app.habit.domain.factory.PromptFactory;
import app.habit.domain.factory.SpecificPhasePreQuestionPromptFactory;
import app.habit.dto.openaidto.FeedbackPromptDto;
import app.habit.dto.openaidto.HabitPreQuestionRs;
import app.habit.dto.openaidto.PhaseAnswerRq;
import app.habit.dto.openaidto.PhaseEvaluationAnswerRq;
import app.habit.dto.openaidto.PhaseEvaluationRq;
import app.habit.dto.openaidto.PhaseEvaluationRs;
import app.habit.dto.openaidto.PhaseFeedbackRq;
import app.habit.dto.openaidto.PhaseFeedbackRs;
import app.habit.dto.openaidto.PhaseQuestionRs;
import app.habit.dto.openaidto.QuestionRs;
import app.habit.dto.openaidto.SingleEvaluationPromptDto;
import app.habit.dto.openaidto.UserHabitPreQuestionRq;
import app.habit.dto.openaidto.UserHabitPreQuestionRs;
import app.habit.repository.FeedbackModuleRepository;
import app.habit.repository.FeedbackSessionRepository;
import app.habit.repository.HabitAssessmentManagerRepository;
import app.habit.repository.HabitFormingPhaseRepository;
import app.habit.repository.HabitRepository;
import app.habit.service.gpt.coach.EvaluationGptCoach;
import app.habit.service.gpt.coach.FeedbackGptCoach;
import app.habit.service.gpt.coach.PhaseGptCoach;
import app.habit.service.gpt.coach.PreQuestionGptCoach;
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

    private final HabitRepository habitRepository;
    private final PreQuestionGptCoach preQuestionGptCoach;
    private final PromptFactory promptFactory;
    private final HabitFormingPhaseService habitFormingPhaseService;
    private final HabitAssessmentManagerService habitAssessmentManagerService;
    private final SubjectService subjectService;
    private final QuestionService questionService;

    private final HabitAssessmentManagerRepository habitAssessmentManagerRepository;
    private final EvaluationPromptFactory evaluationPromptFactory;
    private final EvaluationGptCoach evaluationGptCoach;
    private final AnswerService answerService;

    private final SpecificPhasePreQuestionPromptFactory specificPhasePreQuestionPromptFactory;
    private final PhaseGptCoach phaseGptCoach;
    private final HabitFormingPhaseRepository habitFormingPhaseRepository;
    private final FeedbackModuleService feedbackModuleService;
    private final FeedbackSessionService feedbackSessionService;

    private final FeedbackModuleRepository feedbackModuleRepository;
    private final FeedbackPromptFactory feedbackPromptFactory;
    private final FeedbackGptCoach feedbackGptCoach;
    private final FeedbackSessionRepository feedbackSessionRepository;

    public List<HabitPreQuestionRs> getHabitPreQuestions(Long habitId, String type, String prompt) {
        // request -> gpt
        RequestPrompt requestPrompt = promptFactory.create(type, prompt);
        List<HabitPreQuestionRs> preQuestionRs = preQuestionGptCoach.requestPreQuestions(requestPrompt, url);

        // find
        Habit findHabit = habitRepository.findById(habitId).orElseThrow();

        // save
        Long habitFormingPhaseId = habitFormingPhaseService.save(findHabit.getId());
        Long habitAssessmentManagerId = habitAssessmentManagerService.save(habitFormingPhaseId);

        for (HabitPreQuestionRs preQuestionR : preQuestionRs) {
            String key = preQuestionR.getKey();
            String subject = preQuestionR.getSubject();
            QuestionRs questionRs = preQuestionR.getQuestionRs();

            Long subjectId = subjectService.save(key, subject, habitAssessmentManagerId);
            questionService.save(questionRs.getQuestionKey(), questionRs.getQuestion(), subjectId);
        }

        return preQuestionRs;
    }

    public PhaseEvaluationRs evaluateHabitPhase(PhaseEvaluationRq rq) {
        // find habitFormingPhase
        long habitAssessmentManagerId = rq.getHabitAssessmentManagerId();
        HabitAssessmentManager habitAssessmentManager = habitAssessmentManagerRepository.findById(
                        habitAssessmentManagerId)
                .orElseThrow();

        // save answers according to subject
        for (PhaseEvaluationAnswerRq phaseEvaluationAnswerRq : rq.getAnswers()) {
            answerService.save(phaseEvaluationAnswerRq.getKey(), phaseEvaluationAnswerRq.getUserAnswer(),
                    habitAssessmentManagerId);
        }

        // request -> gpt
        List<SingleEvaluationPromptDto> totalEvaluationPromptDto = subjectService.getTotalEvaluationPromptDto(
                habitAssessmentManager.getId());

        RequestPrompt requestPrompt = evaluationPromptFactory.create(totalEvaluationPromptDto);
        PhaseEvaluationRs evaluationRs = evaluationGptCoach.requestEvaluation(requestPrompt, url);

        // save evaluation result
        String phaseType = evaluationRs.getPhaseType();
        String phaseDescription = evaluationRs.getPhaseDescription();

        habitAssessmentManagerService.savePhaseInfo(phaseType, phaseDescription, habitAssessmentManager.getId());

        return evaluationRs;
    }

    public List<UserHabitPreQuestionRs> getSpecificPhasePreQuestions(UserHabitPreQuestionRq rq) {
        // request -> gpt
        RequestPrompt requestPrompt = specificPhasePreQuestionPromptFactory.create(rq.getHabitFormingPhaseType());
        List<UserHabitPreQuestionRs> userHabitPreQuestionRsList = phaseGptCoach.requestUserHabitPreQuestions(
                requestPrompt, url);

        // find
        HabitFormingPhase userHabitFormingPhase = habitFormingPhaseRepository.findById(rq.getHabitFormingPhaseId())
                .orElseThrow();

        // save 로직
        for (UserHabitPreQuestionRs userHabitPreQuestionRs : userHabitPreQuestionRsList) {
            Long feedbackModuleId = feedbackModuleService.save(userHabitPreQuestionRs.getKey(),
                    userHabitPreQuestionRs.getSubject(), userHabitFormingPhase.getId());

            for (PhaseQuestionRs question : userHabitPreQuestionRs.getQuestions()) {
                feedbackSessionService.save(question.getQuestionKey(), question.getQuestion(), feedbackModuleId);
            }
        }

        // return response to user
        return userHabitPreQuestionRsList;
    }

    public PhaseFeedbackRs getFeedbackAboutSpecificSubject(PhaseFeedbackRq rq) {
        FeedbackModule feedbackModule = feedbackModuleRepository.findById(rq.getFeedbackModuleId())
                .orElseThrow();

        // save answer & create promptDto
        List<FeedbackSession> feedbackSessions = feedbackSessionRepository.findFeedbackSessionsByFeedbackModuleId(
                feedbackModule.getId());

        FeedbackPromptDto feedbackPromptDto = new FeedbackPromptDto(feedbackModule.getSubject());

        for (PhaseAnswerRq phaseAnswer : rq.getPhaseAnswers()) {
            for (FeedbackSession feedbackSession : feedbackSessions) {
                String sessionKey = feedbackSession.getSessionKey();
                if (phaseAnswer.getKey().equals(sessionKey)) {
                    feedbackSession.addAnswer(phaseAnswer.getUserAnswer());
                    feedbackPromptDto.addFeedbackDto(
                            feedbackSession.getSessionKey(),
                            feedbackSession.getQuestion(),
                            feedbackSession.getAnswer()
                    );
                }
            }
        }

        // request -> gpt
        RequestPrompt requestPrompt = feedbackPromptFactory.create(feedbackPromptDto, rq.getHabitFormingPhaseType());
        PhaseFeedbackRs phaseFeedbackRs = feedbackGptCoach.requestPhaseFeedback(requestPrompt, url);

        // save subject & feedback to feedbackModule
        feedbackModule.addSubject(phaseFeedbackRs.getFeedbackSubject());
        feedbackModule.addFeedback(phaseFeedbackRs.getFeedback());

        return phaseFeedbackRs;
    }
}
