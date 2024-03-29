package app.habit.dto.habitdto;

import app.habit.domain.HabitFormingPhaseType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserPhaseInfoDto {

    private HabitFormingPhaseType habitFormingPhaseType;
    private String description;
}
