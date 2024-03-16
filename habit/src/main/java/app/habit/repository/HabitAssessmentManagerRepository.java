package app.habit.repository;

import app.habit.domain.HabitAssessmentManager;
import app.habit.dto.habitdto.UserHabitFormingPhaseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HabitAssessmentManagerRepository extends JpaRepository<HabitAssessmentManager, Long> {

    @Query("SELECT new app.habit.dto.habitdto.UserHabitFormingPhaseDto(hm.habitFormingPhaseId, hm.id, hm.phaseType) FROM HabitAssessmentManager hm WHERE hm.habitFormingPhaseId = :habitFormingPhaseId")
    UserHabitFormingPhaseDto findDtoByHabitFormingPhaseId(@Param("habitFormingPhaseId") Long habitFormingPhaseId);
}
