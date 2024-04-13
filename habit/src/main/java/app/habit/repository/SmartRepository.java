package app.habit.repository;

import app.habit.domain.Smart;
import app.habit.dto.smartdto.UserSmartGoalRs;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SmartRepository extends JpaRepository<Smart, Long> {

    @Query("SELECT new app.habit.dto.smartdto.UserSmartGoalRs(s.goal, s.feedback) FROM Smart s WHERE s.id = :smartId")
    UserSmartGoalRs findUserSmartGoalRsById(@Param("smartId") Long smartId);
}
