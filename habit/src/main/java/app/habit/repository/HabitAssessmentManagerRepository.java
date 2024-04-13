package app.habit.repository;

import app.habit.domain.HabitAssessmentManager;
import app.habit.domain.HabitFormingPhaseType;
import app.habit.dto.habitdto.UserHabitFormingPhaseDto;
import app.habit.dto.habitdto.UserPhaseInfoDto;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HabitAssessmentManagerRepository extends JpaRepository<HabitAssessmentManager, Long> {

    @Query("SELECT new app.habit.dto.habitdto.UserHabitFormingPhaseDto(hm.habitFormingPhaseId, hm.id, hm.phaseType) FROM HabitAssessmentManager hm WHERE hm.habitFormingPhaseId = :habitFormingPhaseId")
    UserHabitFormingPhaseDto findPhaseDtoByHabitFormingPhaseId(@Param("habitFormingPhaseId") Long habitFormingPhaseId);

    @Query("SELECT new app.habit.dto.habitdto.UserPhaseInfoDto(hm.phaseType, hm.phaseDescription) FROM HabitAssessmentManager hm WHERE hm.habitFormingPhaseId = :habitFormingPhaseId")
    UserPhaseInfoDto findUserPhaseInfoDtoByHabitFormingPhaseId(@Param("habitFormingPhaseId") Long habitFormingPhaseId);

    @Query("SELECT hm.phaseType FROM HabitAssessmentManager hm WHERE hm.habitFormingPhaseId = :habitFormingPhaseId")
    Optional<HabitFormingPhaseType> findPhaseTypeByHabitFormingPhaseId(Long habitFormingPhaseId);
}
