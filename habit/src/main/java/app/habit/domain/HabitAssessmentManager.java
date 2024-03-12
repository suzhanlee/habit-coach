package app.habit.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Getter;
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

    @Getter
    @OneToMany(mappedBy = "habitAssessmentManager",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Subject> subjects = new ArrayList<>();

    public HabitAssessmentManager(List<Subject> subjects) {
        addSubjects(subjects);
    }

    private void addSubjects(List<Subject> subjects) {
        this.subjects = subjects;
        for (Subject subject : subjects) {
            subject.addHabitAssessmentManager(this);
        }
    }

    public void assess(String phaseType, String phaseDescription) {
        this.phaseType = HabitFormingPhaseType.findType(phaseType);
        this.phaseDescription = phaseDescription;
    }

    public void addAnswersAccordingToSubject(List<Answer> answers) {
        for (int i = 0; i < this.subjects.size(); i++) {
            this.subjects.get(i).addAnswer(answers.get(i));
        }
    }

    public String createPrompt() {
        return this.subjects.stream().map(Subject::createPrompt).map(prompt -> prompt + '\n')
                .collect(Collectors.joining());
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
