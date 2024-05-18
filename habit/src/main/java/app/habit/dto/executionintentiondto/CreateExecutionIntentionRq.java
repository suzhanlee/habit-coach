package app.habit.dto.executionintentiondto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateExecutionIntentionRq {

    private Long goalTrackerId;
    private String content;
}
