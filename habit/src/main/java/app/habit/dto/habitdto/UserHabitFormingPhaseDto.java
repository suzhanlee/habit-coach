package app.habit.dto.habitdto;

import app.habit.domain.HabitFormingPhaseType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserHabitFormingPhaseDto {

    private long habitFormingPhaseId;
    private long habitAssessmentManagerId;
    private HabitFormingPhaseType habitFormingPhaseType;
}
