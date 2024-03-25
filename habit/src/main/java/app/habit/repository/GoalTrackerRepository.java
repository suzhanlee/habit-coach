package app.habit.repository;

import app.habit.domain.GoalTracker;
import app.habit.dto.habitdto.UserGoalTrackerDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GoalTrackerRepository extends JpaRepository<GoalTracker, Long> {

    @Query("select new app.habit.dto.habitdto.UserGoalTrackerDto(g.id, s.id, e.id) FROM GoalTracker g JOIN Smart s ON s.goalTrackerId = :goalTrackerId JOIN ExecutionIntention e ON e.goalTrackerId = :goalTrackerId")
    UserGoalTrackerDto findDtoById(@Param("goalTrackerId") Long goalTrackerId);
}
