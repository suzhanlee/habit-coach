package app.habit.dto.executionintentiondto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateExecutionIntentionRq {

    private Long executionIntentionId;
    private String content;
}
