package app.habit.service;

import app.habit.domain.GoalTracker;
import app.habit.domain.Habit;
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
import app.habit.service.gpt.request.RequestPrompt;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SmartGoalService {

    private static final String url = "https://api.openai.com/v1/chat/completions";
    private final SmartGoalFeedbackPromptFactory feedbackPromptFactory;
    private final SmartGoalFeedbackGptCoach feedbackGptCoach;

    private final SmartRepository smartRepository;
    private final GoalTrackerRepository goalTrackerRepository;
    private final HabitRepository habitRepository;
    private final HabitFormingPhaseRepository habitFormingPhaseRepository;
    private final HabitAssessmentManagerRepository habitAssessmentManagerRepository;

    public CreateSmartGoalRs createSmartGoal(CreateSmartGoalRq rq) {
        GoalTracker goalTracker = goalTrackerRepository.findById(rq.getGoalTrackerId()).orElseThrow();

        Long habitId = goalTracker.getHabitId();
        HabitFormingPhaseType phaseType = habitAssessmentManagerRepository
                .findPhaseTypeByHabitFormingPhaseId(
                        habitFormingPhaseRepository.findIdByHabitId(habitId).orElseThrow()
                )
                .orElseThrow();

        String habitName = habitRepository.findById(habitId).orElseThrow().getName();
        RequestPrompt requestPrompt = feedbackPromptFactory.create(habitName, phaseType, rq.getGoal());

        String smartGoalFeedback = feedbackGptCoach.requestSmartGoalFeedback(requestPrompt, url);

        Smart smart = new Smart(goalTracker.getId(), rq.getGoal(), smartGoalFeedback);
        smartRepository.save(smart);

        return new CreateSmartGoalRs(smart.getId(), smartGoalFeedback);
    }
}
