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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
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
        Callable<List<HabitPreQuestionRs>> callable = () -> preQuestionGptCoach.requestPreQuestions(requestPrompt,
                url);

        // find
        Habit findHabit = habitRepository.findById(habitId).orElseThrow();

        // save
        Long habitFormingPhaseId = habitFormingPhaseService.findHabitFormingPhaseIdOrCreate(findHabit.getId());
        Long habitAssessmentManagerId = habitAssessmentManagerService.save(habitFormingPhaseId);

        List<HabitPreQuestionRs> habitPreQuestionRsList = getCallList(callable);
        ExecutorService executorService = Executors.newFixedThreadPool(habitPreQuestionRsList.size());
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (HabitPreQuestionRs habitPreQuestionRs : habitPreQuestionRsList) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                String key = habitPreQuestionRs.getKey();
                String subject = habitPreQuestionRs.getSubject();
                QuestionRs questionRs = habitPreQuestionRs.getQuestionRs();

                Long subjectId = subjectService.save(key, subject, habitAssessmentManagerId);
                questionService.save(questionRs.getQuestionKey(), questionRs.getQuestion(), subjectId);
            }, executorService);
            futures.add(future);
        }

        closeFutures(futures, executorService);

        return habitPreQuestionRsList;
    }

    private <T> T getCall(Callable<T> callable) {
        T call;
        try {
            call = callable.call();
        } catch (Exception e) {
            throw new RuntimeException("gpt 코치에게 요청이 돌아오지 않았습니다.");
        }
        return call;
    }

    private void closeFutures(List<CompletableFuture<Void>> futures, ExecutorService executorService) {
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executorService.shutdown();
    }

    public PhaseEvaluationRs evaluateHabitPhase(PhaseEvaluationRq rq) {
        // find habitFormingPhase
        long habitAssessmentManagerId = rq.getHabitAssessmentManagerId();
        HabitAssessmentManager habitAssessmentManager = habitAssessmentManagerRepository.findById(
                        habitAssessmentManagerId)
                .orElseThrow();

        // save answers according to subject
        ExecutorService executorService = Executors.newFixedThreadPool(rq.getAnswers().size());
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (PhaseEvaluationAnswerRq phaseEvaluationAnswerRq : rq.getAnswers()) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() ->
                            answerService.save(phaseEvaluationAnswerRq.getKey(),
                                    phaseEvaluationAnswerRq.getUserAnswer(),
                                    habitAssessmentManagerId),
                    executorService);
            futures.add(future);
        }

        closeFutures(futures, executorService);

        // request -> gpt
        List<SingleEvaluationPromptDto> totalEvaluationPromptDto = subjectService.getTotalEvaluationPromptDto(habitAssessmentManager.getId());
        RequestPrompt requestPrompt = evaluationPromptFactory.create(totalEvaluationPromptDto);
        Callable<PhaseEvaluationRs> callable = () -> evaluationGptCoach.requestEvaluation(requestPrompt, url);

        // save evaluation result
        PhaseEvaluationRs phaseEvaluationRs = getCall(callable);
        String phaseType = phaseEvaluationRs.getPhaseType();
        String phaseDescription = phaseEvaluationRs.getPhaseDescription();

        habitAssessmentManagerService.savePhaseInfo(phaseType, phaseDescription, habitAssessmentManager.getId());

        return phaseEvaluationRs;
    }

    public List<UserHabitPreQuestionRs> getSpecificPhasePreQuestions(UserHabitPreQuestionRq rq) {
        // request -> gpt
        RequestPrompt requestPrompt = specificPhasePreQuestionPromptFactory.create(rq.getHabitFormingPhaseType());
        Callable<List<UserHabitPreQuestionRs>> callable = () -> phaseGptCoach.requestUserHabitPreQuestions(
                requestPrompt, url);

        // find
        HabitFormingPhase userHabitFormingPhase = habitFormingPhaseRepository.findById(rq.getHabitFormingPhaseId())
                .orElseThrow();

        List<UserHabitPreQuestionRs> userHabitPreQuestionRsList = getCallList(callable);

        // save 로직
        for (UserHabitPreQuestionRs userHabitPreQuestionRs : userHabitPreQuestionRsList) {
            Callable<Long> callableFeedbackModuleId = () -> feedbackModuleService.save(userHabitPreQuestionRs.getKey(),
                    userHabitPreQuestionRs.getSubject(), userHabitFormingPhase.getId());

            // save feedbackSessions
            ExecutorService executorService = Executors.newFixedThreadPool(userHabitPreQuestionRsList.size());
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            for (PhaseQuestionRs question : userHabitPreQuestionRs.getQuestions()) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    feedbackSessionService.save(question.getQuestionKey(), question.getQuestion(), getCall(callableFeedbackModuleId));
                }, executorService);
                futures.add(future);
            }
            closeFutures(futures, executorService);
        }

        // return response to user
        return userHabitPreQuestionRsList;
    }

    private <T> List<T> getCallList(Callable<List<T>> callable) {
        List<T> call;
        try {
            call = callable.call();
        } catch (Exception e) {
            throw new RuntimeException("gpt 코치에게 요청이 돌아오지 않았습니다.");
        }
        return call;
    }

    public PhaseFeedbackRs getFeedbackAboutSpecificSubject(PhaseFeedbackRq rq) {
        FeedbackModule feedbackModule = feedbackModuleRepository.findById(rq.getFeedbackModuleId())
                .orElseThrow();

        // save answer & create promptDto
        List<FeedbackSession> feedbackSessions = feedbackSessionRepository.findFeedbackSessionsByFeedbackModuleId(
                feedbackModule.getId());

        FeedbackPromptDto feedbackPromptDto = new FeedbackPromptDto(feedbackModule.getSubject());

        Map<String, FeedbackSession> feedbackSessionMap = feedbackSessions.stream().collect(
                Collectors.toMap(FeedbackSession::getSessionKey, feedbackSession -> feedbackSession, (a, b) -> b));

        for (PhaseAnswerRq phaseAnswer : rq.getPhaseAnswers()) {
            Optional.ofNullable(feedbackSessionMap.get(phaseAnswer.getKey())).ifPresent(feedbackSession -> {
                feedbackSession.addAnswer(phaseAnswer.getUserAnswer());
                feedbackPromptDto.addFeedbackDto(
                        feedbackSession.getSessionKey(),
                        feedbackSession.getQuestion(),
                        feedbackSession.getAnswer()
                );
            });
        }

        // request -> gpt
        RequestPrompt requestPrompt = feedbackPromptFactory.create(feedbackPromptDto, rq.getHabitFormingPhaseType());
        Callable<PhaseFeedbackRs> callable = () -> feedbackGptCoach.requestPhaseFeedback(requestPrompt, url);

        // save subject & feedback to feedbackModule
        PhaseFeedbackRs phaseFeedbackRs = getCall(callable);
        feedbackModule.addSubject(phaseFeedbackRs.getFeedbackSubject());
        feedbackModule.addFeedback(phaseFeedbackRs.getFeedback());

        return phaseFeedbackRs;
    }
}
