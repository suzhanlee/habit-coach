package app.habit.repository;

import app.habit.domain.FeedbackModule;
import app.habit.dto.habitdto.UserHabitFeedbackDto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedbackModuleRepository extends JpaRepository<FeedbackModule, Long> {

    @Query("SELECT new app.habit.dto.habitdto.UserHabitFeedbackDto(fm.id, fm.subject, fm.feedback) FROM FeedbackModule fm WHERE fm.id = :habitFormingPhaseId")
    List<UserHabitFeedbackDto> findUserHabitFeedbackDtoByHabitFormingPhaseId(
            @Param("habitFormingPhaseId") Long habitFormingPhaseId);
}
