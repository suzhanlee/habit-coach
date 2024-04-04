package app.habit.dto.openaidto;

import app.habit.dto.habitdto.UserGoalTrackerDto;
import app.habit.dto.habitdto.UserHabitFormingPhaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SpecificUserHabitInfoRs {

    private String habitName;
    private UserHabitFormingPhaseDto userHabitFormingPhaseDto;
    private UserGoalTrackerDto userGoalTrackerDto;
}
