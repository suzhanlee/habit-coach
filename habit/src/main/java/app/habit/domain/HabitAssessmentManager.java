package app.habit.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class HabitAssessmentManager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "habit_assessment_manager_id")
    @Getter
    private Long id;

    @Enumerated
    private HabitFormingPhaseType phaseType;

    @Column(columnDefinition = "TEXT")
    private String phaseDescription;

    @Column(name = "habit_forming_phase_id")
    @Getter
    private Long habitFormingPhaseId;

    public HabitAssessmentManager(Long id) {
        this.id = id;
    }

    public void assess(String phaseType, String phaseDescription) {
        this.phaseType = HabitFormingPhaseType.findType(phaseType);
        this.phaseDescription = phaseDescription;
    }

    public void addHabitFormingPhaseId(long habitFormingPhaseId) {
        this.habitFormingPhaseId = habitFormingPhaseId;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
