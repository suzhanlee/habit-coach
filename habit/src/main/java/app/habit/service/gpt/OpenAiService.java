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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
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
        // request -> gpt
        log.info("OpenAiService.getHabitPreQuestions 1 : " + Thread.currentThread().getName());
        RequestPrompt requestPrompt = promptFactory.create(type, prompt);

        // find
        Habit findHabit = habitRepository.findById(habitId).orElseThrow();

        // save
        Long habitFormingPhaseId = habitFormingPhaseService.findHabitFormingPhaseIdOrCreate(findHabit.getId());
        Long habitAssessmentManagerId = habitAssessmentManagerService.save(habitFormingPhaseId);

        return CompletableFuture.supplyAsync(
                        () -> {
                            log.info("OpenAiService.getHabitPreQuestions 2 : " + Thread.currentThread().getName());
                            return preQuestionGptCoach.requestPreQuestions(requestPrompt, url);
                        }, executorService)
                .thenComposeAsync(Function.identity(), executorService)
                .thenApplyAsync(habitPreQuestionRsList -> CompletableFuture.allOf(
                                habitPreQuestionRsList.stream()
                                        .map(habitPreQuestionRs -> CompletableFuture.supplyAsync(() -> {
                                            String key = habitPreQuestionRs.getKey();
                                            String subject = habitPreQuestionRs.getSubject();
                                            QuestionRs questionRs = habitPreQuestionRs.getQuestionRs();

                                            return CompletableFuture.supplyAsync(
                                                            () -> (subjectService.save(key, subject, habitAssessmentManagerId)),
                                                            executorService)
                                                    .thenApplyAsync(
                                                            subjectId -> questionService.save(questionRs.getQuestionKey(),
                                                                    questionRs.getQuestion(), subjectId),
                                                            executorService)
                                                    .thenApply(ignored -> habitPreQuestionRs);
                                        }, executorService))
                                        .toArray(CompletableFuture[]::new))
                        .thenApplyAsync(ignored -> habitPreQuestionRsList, executorService), executorService)
                .thenComposeAsync(Function.identity(), executorService);
    }

    public CompletableFuture<PhaseEvaluationRs> evaluateHabitPhase(PhaseEvaluationRq rq) {
        // find habitFormingPhase
        log.info("OpenAiService.evaluateHabitPhase 1 : " + Thread.currentThread().getName());
        long habitAssessmentManagerId = rq.getHabitAssessmentManagerId();
        HabitAssessmentManager habitAssessmentManager = habitAssessmentManagerRepository.findById(
                        habitAssessmentManagerId)
                .orElseThrow();

        // save answers according to subject
        return CompletableFuture.allOf(
                        rq.getAnswers().stream()
                                .map(phaseEvaluationAnswerRq -> CompletableFuture.runAsync(() -> {
                                    log.info("OpenAiService.evaluateHabitPhase 2 : " + Thread.currentThread().getName());
                                    answerService.save(phaseEvaluationAnswerRq.getKey(),
                                            phaseEvaluationAnswerRq.getUserAnswer(),
                                            habitAssessmentManagerId);
                                }, executorService))
                                .toArray(CompletableFuture[]::new)
                        // request -> gpt
                ).supplyAsync(() -> {
                    List<SingleEvaluationPromptDto> totalEvaluationPromptDto = subjectService.getTotalEvaluationPromptDto(
                            habitAssessmentManager.getId());
                    return evaluationPromptFactory.create(totalEvaluationPromptDto);
                }, executorService)
                // return
                .thenComposeAsync(requestPrompt -> {
                    CompletableFuture<PhaseEvaluationRs> phaseEvaluationRsFuture = evaluationGptCoach.requestEvaluation(
                            requestPrompt, url);

                    // save evaluation result
                    phaseEvaluationRsFuture.thenAcceptAsync(phaseEvaluationRs -> {
                        String phaseType = phaseEvaluationRs.getPhaseType();
                        String phaseDescription = phaseEvaluationRs.getPhaseDescription();
                        habitAssessmentManagerService.savePhaseInfo(phaseType, phaseDescription,
                                habitAssessmentManager.getId());
                    }, executorService);

                    return phaseEvaluationRsFuture;
                }, executorService);
//                .thenComposeAsync(Function.identity(), executorService);
    }

    public CompletableFuture<List<UserHabitPreQuestionRs>> getSpecificPhasePreQuestions(UserHabitPreQuestionRq rq) {
        // request -> gpt
        log.info("OpenAiService.getSpecificPhasePreQuestions 1 : " + Thread.currentThread().getName());
        RequestPrompt requestPrompt = specificPhasePreQuestionPromptFactory.create(rq.getHabitFormingPhaseType());

        // find => 이걸 한 칸 앞으로 로직을 이동시켰는지 좀 더 확인해보자. 괜찮을지 아닐지
        HabitFormingPhase userHabitFormingPhase = habitFormingPhaseRepository.findById(rq.getHabitFormingPhaseId())
                .orElseThrow();

        // save 로직
        return CompletableFuture.supplyAsync(
                        () -> {
                            log.info("OpenAiService.getSpecificPhasePreQuestions 2 : " + Thread.currentThread().getName());
                            return phaseGptCoach.requestUserHabitPreQuestions(requestPrompt, url);
                        }, executorService)
                .thenComposeAsync(Function.identity(), executorService)
                .thenApplyAsync(userHabitPreQuestionRsList -> {
                    for (UserHabitPreQuestionRs userHabitPreQuestionRs : userHabitPreQuestionRsList) {
                        Long feedbackModuleId = feedbackModuleService.save(userHabitPreQuestionRs.getKey(),
                                userHabitPreQuestionRs.getSubject(), userHabitFormingPhase.getId());

                        // save feedbackSessions
                        for (PhaseQuestionRs question : userHabitPreQuestionRs.getQuestions()) {
                            CompletableFuture.runAsync(
                                    () -> feedbackSessionService.save(question.getQuestionKey(),
                                            question.getQuestion(),
                                            feedbackModuleId), executorService);
                        }
                    }
                    return userHabitPreQuestionRsList;
                }, executorService);
    }

    public CompletableFuture<PhaseFeedbackRs> getFeedbackAboutSpecificSubject(PhaseFeedbackRq rq) {
        log.info("OpenAiService.getFeedbackAboutSpecificSubject 1 : " + Thread.currentThread().getName());
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
        RequestPrompt requestPrompt = feedbackPromptFactory.create(feedbackPromptDto,
                rq.getHabitFormingPhaseType());

        return CompletableFuture.supplyAsync(() -> {
                    log.info("OpenAiService.getFeedbackAboutSpecificSubject 2 : " + Thread.currentThread().getName());
                    // request phase feedback asynchronously
                    return feedbackGptCoach.requestPhaseFeedback(requestPrompt, url)
                            .thenApplyAsync(phaseFeedbackRs -> {
                                // add subject & feedback to feedbackModule
                                feedbackModule.addSubject(phaseFeedbackRs.getFeedbackSubject());
                                feedbackModule.addFeedback(phaseFeedbackRs.getFeedback());
                                return phaseFeedbackRs;
                            }, executorService);
                }, executorService)
                .thenComposeAsync(Function.identity(), executorService);
    }
}
