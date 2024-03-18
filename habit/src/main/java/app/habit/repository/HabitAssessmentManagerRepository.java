package app.habit.repository;

import app.habit.domain.HabitAssessmentManager;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HabitAssessmentManagerRepository extends JpaRepository<HabitAssessmentManager, Long> {
}
