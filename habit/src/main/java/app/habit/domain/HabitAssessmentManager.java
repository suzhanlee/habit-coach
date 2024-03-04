package app.habit.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class HabitAssessmentManager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "habit_assessment_manager_id")
    private Long id;

    @Enumerated
    private HabitFormingPhaseType phaseType;

    @Column(columnDefinition = "TEXT")
    private String phaseDescription;

    @OneToMany(mappedBy = "habitAssessmentManager",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Subject> subjects = new ArrayList<>();

    public HabitAssessmentManager(List<Subject> subjects) {
        this.subjects = subjects;
    }

    public void evaluate(HabitFormingPhaseType phaseType, String phaseDescription) {
        this.phaseType = phaseType;
        this.phaseDescription = phaseDescription;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HabitAssessmentManager that = (HabitAssessmentManager) o;
        return Objects.equals(subjects, that.subjects);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subjects);
    }
}
