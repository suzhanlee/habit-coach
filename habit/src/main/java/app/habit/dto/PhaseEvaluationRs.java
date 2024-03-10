package app.habit.dto;

import app.habit.domain.HabitFormingPhaseType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PhaseEvaluationRs {

    private long habitAssessmentManagerId;
    private HabitFormingPhaseType phaseType;
    private String phaseDescription;
}
