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
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
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

    private final ExecutorService executorService;

    public CompletableFuture<List<HabitPreQuestionRs>> getHabitPreQuestions(Long habitId, String type, String prompt) {
        System.out.println("OpenAiService.getHabitPreQuestions1 : " + Thread.currentThread());
        // request -> gpt
        RequestPrompt requestPrompt = promptFactory.create(type, prompt);

        CompletableFuture<List<HabitPreQuestionRs>> futureHabitPreQuestions = CompletableFuture.supplyAsync(
                () -> {
                    System.out.println("OpenAiService.requestPreQuestions : " + Thread.currentThread());
                    return preQuestionGptCoach.requestPreQuestions(requestPrompt, url);
                },
                executorService
        ).thenComposeAsync(Function.identity(), executorService);

        System.out.println("OpenAiService.getHabitPreQuestions2 : " + Thread.currentThread());

        // find
        Habit findHabit = habitRepository.findById(habitId).orElseThrow();

        // save
        Long habitFormingPhaseId = habitFormingPhaseService.findHabitFormingPhaseIdOrCreate(findHabit.getId());
        Long habitAssessmentManagerId = habitAssessmentManagerService.save(habitFormingPhaseId);

        System.out.println("OpenAiService.getHabitPreQuestions3 : " + Thread.currentThread());

        return futureHabitPreQuestions.thenComposeAsync(habitPreQuestionRsList -> {
            System.out.println("OpenAiService.getHabitPreQuestions4 : " + Thread.currentThread());
            List<CompletableFuture<HabitPreQuestionRs>> processedFutures = habitPreQuestionRsList.stream()
                    .map(habitPreQuestionRs -> CompletableFuture.supplyAsync(() -> {
                        System.out.println("OpenAiService.getHabitPreQuestions 100 : " + Thread.currentThread());
                        String key = habitPreQuestionRs.getKey();
                        String subject = habitPreQuestionRs.getSubject();
                        QuestionRs questionRs = habitPreQuestionRs.getQuestionRs();

                        Long subjectId = subjectService.save(key, subject, habitAssessmentManagerId);
                        questionService.save(questionRs.getQuestionKey(), questionRs.getQuestion(), subjectId);

                        return habitPreQuestionRs;
                    }, executorService))
                    .toList();

            CompletableFuture<Void> future = CompletableFuture.allOf(
                    processedFutures.toArray(new CompletableFuture[0]));
            return future.thenApplyAsync(v -> processedFutures.stream()
                    .map(CompletableFuture::join)
                    .toList(), executorService);
        }, executorService);
    }

    public CompletableFuture<PhaseEvaluationRs> evaluateHabitPhase(PhaseEvaluationRq rq) {
        // find habitFormingPhase
        long habitAssessmentManagerId = rq.getHabitAssessmentManagerId();
        HabitAssessmentManager habitAssessmentManager = habitAssessmentManagerRepository.findById(
                        habitAssessmentManagerId)
                .orElseThrow();

        // save answers according to subject
        CompletableFuture<Void> saveAnswersFuture = CompletableFuture.allOf(
                rq.getAnswers().stream()
                        .map(phaseEvaluationAnswerRq -> CompletableFuture.runAsync(() -> {
                            answerService.save(phaseEvaluationAnswerRq.getKey(),
                                    phaseEvaluationAnswerRq.getUserAnswer(),
                                    habitAssessmentManagerId);
                        }, executorService))
                        .toArray(CompletableFuture[]::new)
        );

        // request -> gpt
        CompletableFuture<RequestPrompt> totalEvaluationPromptFuture = saveAnswersFuture.thenApplyAsync(
                Void -> {
                    List<SingleEvaluationPromptDto> totalEvaluationPromptDto = subjectService.getTotalEvaluationPromptDto(
                            habitAssessmentManager.getId());
                    return evaluationPromptFactory.create(totalEvaluationPromptDto);
                }, executorService);

        return totalEvaluationPromptFuture.thenApplyAsync(requestPrompt -> {
                    CompletableFuture<PhaseEvaluationRs> phaseEvaluationRsFuture = evaluationGptCoach.requestEvaluation(
                            requestPrompt, url);

                    // save evaluation result
                    phaseEvaluationRsFuture.thenApplyAsync(phaseEvaluationRs -> {
                        String phaseType = phaseEvaluationRs.getPhaseType();
                        String phaseDescription = phaseEvaluationRs.getPhaseDescription();
                        habitAssessmentManagerService.savePhaseInfo(phaseType, phaseDescription,
                                habitAssessmentManager.getId());
                        return phaseEvaluationRsFuture;
                    }, executorService);

                    return phaseEvaluationRsFuture;
                }, executorService)
                .thenComposeAsync(Function.identity(), executorService);
    }

    public CompletableFuture<List<UserHabitPreQuestionRs>> getSpecificPhasePreQuestions(UserHabitPreQuestionRq rq) {
        // request -> gpt
        RequestPrompt requestPrompt = specificPhasePreQuestionPromptFactory.create(rq.getHabitFormingPhaseType());
        CompletableFuture<List<UserHabitPreQuestionRs>> futureUserHabitPreQuestionList = CompletableFuture.supplyAsync(
                        () -> phaseGptCoach.requestUserHabitPreQuestions(
                                requestPrompt, url), executorService)
                .thenComposeAsync(Function.identity(), executorService);

        // find
        HabitFormingPhase userHabitFormingPhase = habitFormingPhaseRepository.findById(rq.getHabitFormingPhaseId())
                .orElseThrow();

        // save 로직
        futureUserHabitPreQuestionList.thenAcceptAsync(userHabitPreQuestionRsList -> {
            for (UserHabitPreQuestionRs userHabitPreQuestionRs : userHabitPreQuestionRsList) {
                Long feedbackModuleId = feedbackModuleService.save(userHabitPreQuestionRs.getKey(),
                        userHabitPreQuestionRs.getSubject(), userHabitFormingPhase.getId());

                // save feedbackSessions
                for (PhaseQuestionRs question : userHabitPreQuestionRs.getQuestions()) {
                    CompletableFuture.runAsync(
                            () -> feedbackSessionService.save(question.getQuestionKey(), question.getQuestion(),
                                    feedbackModuleId), executorService);
                }
            }
        }, executorService);

        // return response to user
        return futureUserHabitPreQuestionList;
    }

    public CompletableFuture<PhaseFeedbackRs> getFeedbackAboutSpecificSubject(PhaseFeedbackRq rq) {
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
        return CompletableFuture.supplyAsync(() -> {
                            CompletableFuture<PhaseFeedbackRs> processedFuture = feedbackGptCoach.requestPhaseFeedback(
                                    requestPrompt, url);

                            // save subject & feedback to feedbackModule
                            processedFuture.thenAcceptAsync(phaseFeedbackRs -> {
                                feedbackModule.addSubject(phaseFeedbackRs.getFeedbackSubject());
                                feedbackModule.addFeedback(phaseFeedbackRs.getFeedback());
                            });
                            return processedFuture;
                        }
                        , executorService)
                .thenComposeAsync(Function.identity(), executorService);
    }
}
