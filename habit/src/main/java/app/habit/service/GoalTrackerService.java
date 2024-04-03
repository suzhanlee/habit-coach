package app.habit.service;

import app.habit.domain.GoalTracker;
import app.habit.domain.Habit;
import app.habit.repository.GoalTrackerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class GoalTrackerService {

    private final GoalTrackerRepository goalTrackerRepository;

    public Long findGoalTrackerIdOrCreate(Habit habit) {
        return goalTrackerRepository.findByHabitId(habit.getId())
                .orElseGet(() -> createGoalTracker(habit));
    }

    private Long createGoalTracker(Habit habit) {
        GoalTracker goalTracker = new GoalTracker(habit.getId());
        goalTrackerRepository.save(goalTracker);
        return goalTracker.getId();
    }
}
