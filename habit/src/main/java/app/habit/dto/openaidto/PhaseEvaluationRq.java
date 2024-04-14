package app.habit.dto.openaidto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PhaseEvaluationRq {

    private long habitAssessmentManagerId;
    private List<PhaseEvaluationAnswerRq> answers;
}
