package app.habit.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subject_id")
    private Long id;
    private String subjectKey;
    private String subject;

    @Column(name = "habit_assessment_manager_id")
    private Long habitAssessmentManagerId;

    public void addHabitAssessmentManagerId(Long habitAssessmentManagerId) {
        this.habitAssessmentManagerId = habitAssessmentManagerId;
    }

    public Subject(String subjectKey, String subjectContent) {
        this.subjectKey = subjectKey;
        this.subject = subjectContent;
    }
}
