package app.habit.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class FeedbackModule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_module_id")
    private Long id;

    @Column(name = "subject_key")
    private String key;

    private String subject;

    private String feedback;

    @Column(name = "habit_forming_phase_id")
    private Long habitFormingPhaseId;

    public FeedbackModule(String key, String subject) {
        this.key = key;
        this.subject = subject;
    }

    public FeedbackModule(Long id, String subject) {
        this.id = id;
        this.subject = subject;
    }

    public void addSubject(String subject) {
        this.subject = subject;
    }

    public void addFeedback(String feedback) {
        this.feedback = feedback;
    }

    public void addHabitFormingPhaseId(Long habitFormingPhaseId) {
        this.habitFormingPhaseId = habitFormingPhaseId;
    }
}
