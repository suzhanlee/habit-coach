package app.habit.repository;

import app.habit.domain.Habit;
import app.habit.dto.habitdto.UserHabitListRs;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HabitRepository extends JpaRepository<Habit, Long> {

    @Query("SELECT new app.habit.dto.habitdto.UserHabitListRs(h.id, h.name) FROM Habit h WHERE h.userId = :userId")
    List<UserHabitListRs> findUserHabitListByUserId(@Param("userId") long userId);

    @Query("SELECT g.id FROM Habit h JOIN GoalTracker g ON g.habitId = :habitId")
    Optional<Long> findGoalTrackerIdById(@Param("habitId") Long habitId);
}
