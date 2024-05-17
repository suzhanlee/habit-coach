package app.habit.service;

import app.habit.domain.GoalTracker;
import app.habit.domain.HabitFormingPhaseType;
import app.habit.domain.Smart;
import app.habit.domain.factory.SmartGoalFeedbackPromptFactory;
import app.habit.dto.smartdto.CreateSmartGoalRq;
import app.habit.dto.smartdto.CreateSmartGoalRs;
import app.habit.dto.smartdto.UpdateSmartGoalRq;
import app.habit.dto.smartdto.UpdateSmartGoalRs;
import app.habit.dto.smartdto.UserSmartGoalRs;
import app.habit.repository.GoalTrackerRepository;
import app.habit.repository.HabitAssessmentManagerRepository;
import app.habit.repository.HabitFormingPhaseRepository;
import app.habit.repository.HabitRepository;
import app.habit.repository.SmartRepository;
import app.habit.service.gpt.coach.SmartGoalFeedbackGptCoach;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SmartGoalService {

    private static final String url = "https://api.openai.com/v1/chat/completions";

    private final GoalTrackerRepository goalTrackerRepository;
    private final HabitAssessmentManagerRepository habitAssessmentManagerRepository;
    private final HabitFormingPhaseRepository habitFormingPhaseRepository;
    private final HabitRepository habitRepository;
    private final SmartGoalFeedbackPromptFactory feedbackPromptFactory;
    private final SmartGoalFeedbackGptCoach feedbackGptCoach;
    private final SmartRepository smartRepository;

    private final ExecutorService executorService;

    public CompletableFuture<CreateSmartGoalRs> createSmartGoal(CreateSmartGoalRq rq) {
        GoalTracker goalTracker = goalTrackerRepository.findById(rq.getGoalTrackerId()).orElseThrow();

        Long habitId = goalTracker.getHabitId();
        HabitFormingPhaseType phaseType = habitAssessmentManagerRepository
                .findPhaseTypeByHabitFormingPhaseId(
                        habitFormingPhaseRepository.findIdByHabitId(habitId).orElseThrow()
                )
                .orElseThrow();

        String habitName = habitRepository.findById(habitId).orElseThrow().getName();

        return CompletableFuture
                .supplyAsync(() -> feedbackPromptFactory.create(habitName, phaseType, rq.getGoal()), executorService)
                .thenApplyAsync(requestPrompt -> feedbackGptCoach.requestSmartGoalFeedback(requestPrompt, url),
                        executorService)
                .thenComposeAsync(Function.identity(), executorService)
                .thenApplyAsync(feedback -> {
                    Smart smart = new Smart(goalTracker.getId(), rq.getGoal(), feedback);
                    smartRepository.save(smart);
                    return new CreateSmartGoalRs(smart.getId(), feedback);
                }, executorService);
    }

    public UserSmartGoalRs getSmartGoal(long smartGoalId) {
        return smartRepository.findUserSmartGoalRsById(smartGoalId);
    }

    public void deleteSmartGoal(long smartGoalId) {
        smartRepository.deleteById(smartGoalId);
    }

    public CompletableFuture<UpdateSmartGoalRs> updateSmartGoal(UpdateSmartGoalRq rq) {
        Long goalTrackerId = smartRepository.findById(rq.getSmartId())
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
                .supplyAsync(() -> feedbackPromptFactory.create(habitName, phaseType, rq.getGoal()), executorService)
                .thenApplyAsync(requestPrompt -> feedbackGptCoach.requestSmartGoalFeedback(requestPrompt, url),
                        executorService)
                .thenComposeAsync(Function.identity(), executorService)
                .thenApplyAsync(UpdateSmartGoalRs::new, executorService);
    }
}
