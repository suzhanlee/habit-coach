package app.habit.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PhaseEvaluationRq {

    private long habitFormingPhaseId;
    private List<PhaseEvaluationAnswerRq> answers;
}
