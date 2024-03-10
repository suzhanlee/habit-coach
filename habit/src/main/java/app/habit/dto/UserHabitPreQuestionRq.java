package app.habit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserHabitPreQuestionRq {

    private long habitFormingPhaseId;
    private String habitFormingPhaseType;
}
