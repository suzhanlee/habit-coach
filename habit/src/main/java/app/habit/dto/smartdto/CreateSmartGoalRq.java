package app.habit.dto.smartdto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateSmartGoalRq {

    private long goalTrackerId;
    private String goal;
}
