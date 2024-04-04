package app.habit.repository;

import app.habit.domain.HabitFormingPhase;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HabitFormingPhaseRepository extends JpaRepository<HabitFormingPhase, Long> {

    @Query("SELECT hp.id FROM HabitFormingPhase hp WHERE hp.habitId = :habitId")
    Optional<Long> findIdByHabitId(@Param("habitId") Long habitId);
}
