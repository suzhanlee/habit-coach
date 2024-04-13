package app.habit.dto.smartdto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateSmartGoalRs {

    private long smartId;
    private String feedback;
}
