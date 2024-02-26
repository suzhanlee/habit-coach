package app.habit.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subject_id")
    private Long id;
    private String subjectKey;
    private String subject;

    @JoinColumn(name = "question_id")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Question question;

    @JoinColumn(name = "answer_id")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Answer answer;

    @JoinColumn(name = "habit_assessment_manager_id")
    @ManyToOne
    private HabitAssessmentManager habitAssessmentManager;

    public Subject(String subjectKey, String subject, Question question) {
        this.subjectKey = subjectKey;
        this.subject = subject;
        this.question = question;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Subject subject1 = (Subject) o;
        return Objects.equals(subjectKey, subject1.subjectKey) && Objects.equals(subject,
                subject1.subject) && Objects.equals(question, subject1.question) && Objects.equals(
                answer, subject1.answer) && Objects.equals(habitAssessmentManager,
                subject1.habitAssessmentManager);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subjectKey, subject, question, answer, habitAssessmentManager);
    }
}
