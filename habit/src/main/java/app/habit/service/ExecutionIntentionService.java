package app.habit.service;

import app.habit.domain.ExecutionIntention;
import app.habit.domain.GoalTracker;
import app.habit.domain.HabitFormingPhaseType;
import app.habit.domain.factory.ExecutionIntentionFeedbackPromptFactory;
import app.habit.dto.executionintentiondto.CreateExecutionIntentionRq;
import app.habit.dto.executionintentiondto.CreateExecutionIntentionRs;
import app.habit.dto.executionintentiondto.UpdateExecutionIntentionRq;
import app.habit.dto.executionintentiondto.UpdateExecutionIntentionRs;
import app.habit.dto.executionintentiondto.UserExecutionIntentionRs;
import app.habit.repository.ExecutionIntentionRepository;
import app.habit.repository.GoalTrackerRepository;
import app.habit.repository.HabitAssessmentManagerRepository;
import app.habit.repository.HabitFormingPhaseRepository;
import app.habit.repository.HabitRepository;
import app.habit.service.gpt.coach.ExecutionIntentionFeedbackGptCoach;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ExecutionIntentionService {

    private static final String url = "https://api.openai.com/v1/chat/completions";

    private final GoalTrackerRepository goalTrackerRepository;
    private final HabitFormingPhaseRepository habitFormingPhaseRepository;
    private final HabitAssessmentManagerRepository habitAssessmentManagerRepository;
    private final HabitRepository habitRepository;
    private final ExecutionIntentionFeedbackPromptFactory feedbackPromptFactory;
    private final ExecutionIntentionFeedbackGptCoach feedbackGptCoach;
    private final ExecutionIntentionRepository executionIntentionRepository;

    private final ExecutorService executorService;

    public CompletableFuture<CreateExecutionIntentionRs> createExecutionIntention(CreateExecutionIntentionRq rq) {
        GoalTracker goalTracker = goalTrackerRepository.findById(rq.getGoalTrackerId()).orElseThrow();

        Long habitId = goalTracker.getHabitId();
        HabitFormingPhaseType phaseType = habitAssessmentManagerRepository
                .findPhaseTypeByHabitFormingPhaseId(
                        habitFormingPhaseRepository.findIdByHabitId(habitId).orElseThrow()
                )
                .orElseThrow();

        String habitName = habitRepository.findById(habitId).orElseThrow().getName();

        return CompletableFuture
                .supplyAsync(() -> feedbackPromptFactory.create(habitName, phaseType, rq.getContent()), executorService)
                .thenApplyAsync(requestPrompt -> feedbackGptCoach.requestExecutionIntentionFeedback(requestPrompt, url),
                        executorService)
                .thenComposeAsync(Function.identity(), executorService)
                .thenApplyAsync(feedback -> {
                    ExecutionIntention executionIntention = new ExecutionIntention(goalTracker.getId(), rq.getContent(),
                            feedback);
                    executionIntentionRepository.save(executionIntention);
                    return new CreateExecutionIntentionRs(executionIntention.getId(), feedback);
                }, executorService);
    }

    public UserExecutionIntentionRs getExecutionIntention(long executionIntentionId) {
        return executionIntentionRepository.findUserSmartGoalRsById(executionIntentionId).orElseThrow();
    }

    public void deleteExecutionIntention(long executionIntentionId) {
        executionIntentionRepository.deleteById(executionIntentionId);
    }

    public CompletableFuture<UpdateExecutionIntentionRs> updateExecutionIntention(UpdateExecutionIntentionRq rq) {
        Long goalTrackerId = executionIntentionRepository.findById(rq.getExecutionIntentionId())
                .orElseThrow()
                .getGoalTrackerId();

        Long habitId = goalTrackerRepository.findById(goalTrackerId)
                .orElseThrow()
                .getHabitId();

        String habitName = habitRepository.findById(habitId)
                .orElseThrow()
                .getName();

        HabitFormingPhaseType phaseType = habitAssessmentManagerRepository.findPhaseTypeByHabitFormingPhaseId(
                        habitFormingPhaseRepository.findIdByHabitId(habitId).orElseThrow())
                .orElseThrow();

        return CompletableFuture
                .supplyAsync(() -> feedbackPromptFactory.create(habitName, phaseType, rq.getContent()), executorService)
                .thenApplyAsync(requestPrompt -> feedbackGptCoach.requestExecutionIntentionFeedback(requestPrompt, url),
                        executorService)
                .thenComposeAsync(Function.identity(), executorService)
                .thenApplyAsync(UpdateExecutionIntentionRs::new, executorService);
    }
}
