package app.habit.dto.habitdto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserGoalTrackerDto {

    private long goalTrackerId;
    private long smartId;
    private long executionIntentionId;
}
