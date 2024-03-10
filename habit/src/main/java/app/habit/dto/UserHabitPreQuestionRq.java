package app.habit.dto;

import app.habit.domain.HabitFormingPhaseType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserHabitPreQuestionRq {

    private long habitFormingPhaseId;
    private HabitFormingPhaseType phaseType;
}
