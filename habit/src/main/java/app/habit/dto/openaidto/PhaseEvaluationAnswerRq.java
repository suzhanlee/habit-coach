package app.habit.dto.openaidto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PhaseEvaluationAnswerRq {

    private String key;
    private String userAnswer;
}
