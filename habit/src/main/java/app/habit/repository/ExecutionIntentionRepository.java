package app.habit.repository;

import app.habit.domain.ExecutionIntention;
import app.habit.dto.executionintentiondto.UserExecutionIntentionRs;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExecutionIntentionRepository extends JpaRepository<ExecutionIntention, Long> {

    @Query("SELECT new app.habit.dto.executionintentiondto.UserExecutionIntentionRs(e.content, e.feedback) FROM ExecutionIntention e WHERE e.id = :executionIntentionId")
    Optional<UserExecutionIntentionRs> findUserSmartGoalRsById(@Param("executionIntentionId") Long executionIntentionId);
}
