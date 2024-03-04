package app.habit.repository;

import app.habit.domain.FeedbackModule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackModuleRepository extends JpaRepository<FeedbackModule, Long> {
}
