package app.habit.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class FeedbackSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    private String sessionKey;
    private String question;

    // 4 번째 api 부터 필요한 필드
    @Getter
    private String answer;

    @ManyToOne
    @JoinColumn(name = "feedback_module_id")
    private FeedbackModule feedbackModule;

    public FeedbackSession(String sessionKey, String question, String answer) {
        this.sessionKey = sessionKey;
        this.question = question;
        this.answer = answer;
    }

    public FeedbackSession(String sessionKey, String question) {
        this.sessionKey = sessionKey;
        this.question = question;
    }

    public void addAnswer(String answer) {
        this.answer = answer;
    }

    public void addFeedbackModule(FeedbackModule feedbackModule) {
        this.feedbackModule = feedbackModule;
    }

    public boolean hasSameKey(String key) {
        return this.sessionKey.equals(key);
    }

    public String createPrompt() {
        return "number : " + this.sessionKey + '\n'
                + "question : " + this.question + '\n'
                + "answer : " + this.answer + '\n';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FeedbackSession that = (FeedbackSession) o;
        return Objects.equals(sessionKey, that.sessionKey) && Objects.equals(question, that.question);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionKey, question);
    }
}
