package app.habit.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private String answer;
    private String feedback;

    public FeedbackSession(String sessionKey, String question) {
        this.sessionKey = sessionKey;
        this.question = question;
    }

    public void addAnswer(String answer) {
        this.answer = answer;
    }

    public void addFeedback(String feedback) {
        this.feedback = feedback;
    }

    public boolean isSameSubject(String feedbackSubject) {
        return false;
    }
}
