package app.habit.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private Long id;
    private String key;
    private String subject;

    @Getter
    @OneToMany(mappedBy = "feedbackSession",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedbackSession> feedbackSessions = new ArrayList<>();

    public FeedbackModule(String key, String subject) {
        this.key = key;
        this.subject = subject;
    }

    public void addSession(FeedbackSession feedbackSession) {
        this.feedbackSessions.add(feedbackSession);
    }
}
