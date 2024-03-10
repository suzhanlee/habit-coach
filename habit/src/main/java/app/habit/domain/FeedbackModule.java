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
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class FeedbackModule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_module_id")
    private Long id;
    @Column(name = "subject_key")
    private String key;
    private String subject;
    private String feedback;

    @ManyToOne
    @JoinColumn(name = "habit_forming_phase_id")
    private HabitFormingPhase habitFormingPhase;

    @Getter
    @OneToMany(mappedBy = "feedbackModule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedbackSession> feedbackSessions = new ArrayList<>();

    public FeedbackModule(String subject, List<FeedbackSession> feedbackSessions) {
        this.subject = subject;
        this.feedbackSessions = feedbackSessions;
    }

    public FeedbackModule(String key, String subject) {
        this.key = key;
        this.subject = subject;
    }

    public void addSession(FeedbackSession feedbackSession) {
        this.feedbackSessions.add(feedbackSession);
        feedbackSession.addFeedbackModule(this);
    }

    public void addSubject(String subject) {
        this.subject = subject;
    }

    public void addFeedback(String feedback) {
        this.feedback = feedback;
    }

    public void addHabitFormingPhase(HabitFormingPhase habitFormingPhase) {
        this.habitFormingPhase = habitFormingPhase;
    }

    public void addAnswer(String key, String answer) {
        FeedbackSession feedbackSession = findFeedbackSessionByKey(key);
        feedbackSession.addAnswer(answer);
    }

    private FeedbackSession findFeedbackSessionByKey(String key) {
        return feedbackSessions.stream()
                .filter(feedbackSession -> feedbackSession.hasSameKey(key))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public String createPrompt() {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("subject : ").append(this.subject).append('\n');
        for (FeedbackSession feedbackSession : this.feedbackSessions) {
            promptBuilder.append(feedbackSession.createPrompt());
        }
        return promptBuilder.toString();
    }
}
