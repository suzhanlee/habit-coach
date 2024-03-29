package app.habit.dto.habitdto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserHabitPhaseFeedbackRs {

    private String habitFormingPhaseType;
    private String description;
    private List<UserHabitFeedbackModule> userHabitFeedbackModules;

    public UserHabitPhaseFeedbackRs(UserPhaseInfoDto userPhaseInfoDto, List<UserHabitFeedbackModule> userHabitFeedbackModules) {
        this.habitFormingPhaseType = userPhaseInfoDto.getHabitFormingPhaseType().toString();
        this.description = userPhaseInfoDto.getDescription();
        this.userHabitFeedbackModules = userHabitFeedbackModules;
    }
}
