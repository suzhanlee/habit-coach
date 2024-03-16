package app.habit.repository;

import app.habit.domain.FeedbackSession;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedbackSessionRepository extends JpaRepository<FeedbackSession, Long> {

    @Query("SELECT fs FROM FeedbackSession fs WHERE fs.feedbackModuleId = :feedbackModuleId")
    List<FeedbackSession> findFeedbackSessionsByFeedbackModuleId(@Param("feedbackModuleId") Long feedbackModuleId);
}
