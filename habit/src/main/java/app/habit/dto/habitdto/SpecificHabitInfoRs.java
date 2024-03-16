package app.habit.dto.habitdto;

import app.habit.domain.HabitFormingPhaseType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SpecificHabitInfoRs {

    private String name;
    private long habitFormingPhaseId;
    private long habitAssessmentManagerId;
    private HabitFormingPhaseType habitFormingPhaseType;
    private long goalTrackerId;
    private long smartId;
    private long executionIntentionId;
//    @Getter
//    @NoArgsConstructor
//    @AllArgsConstructor
//    static class HabitFormingPhaseRs {
//        private long habitFormingPhaseId;
//        private long habitAssessmentManagerId;
//        private long habitFormingPhaseType;
//    }
//
//    @Getter
//    @NoArgsConstructor
//    @AllArgsConstructor
//    static class HabitGoalTrackerRs {
//        private long goalTrackerId;
//        private long smartId;
//        private long executionIntentionId;
//    }
}
