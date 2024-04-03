package app.habit.service;

import app.habit.domain.GoalTracker;
import app.habit.domain.Habit;
import app.habit.repository.GoalTrackerRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class GoalTrackerService {

    private final GoalTrackerRepository goalTrackerRepository;

    public Long findGoalTracker(Habit habit) {
        Optional<Long> habitId = goalTrackerRepository.findByHabitId(habit.getId());
        if (habitId.isEmpty()) {
            GoalTracker goalTracker = new GoalTracker(habit.getId());
            goalTrackerRepository.save(goalTracker);
            return goalTracker.getId();
        }
        return habitId.get();
    }
}
