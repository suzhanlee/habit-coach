package app.habit.repository;

import app.habit.domain.HabitFormingPhase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HabitFormingPhaseRepository extends JpaRepository<HabitFormingPhase, Long> {
}
