package app.habit.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long id;
    private String answerKey;
    private String answer;

    public Answer(String answerKey, String answer) {
        this.answerKey = answerKey;
        this.answer = answer;
    }

    public String createPrompt(String subject, String questionPrompt) {
        return createSubTitle(subject) + createQuestion(questionPrompt) + createAnswer();
    }

    private String createSubTitle(String subject) {
        return "## " + answerKey + ". " + subject + '\n';
    }

    private String createAnswer() {
        return this.answer + '\n';
    }

    private String createQuestion(String questionPrompt) {
        return questionPrompt + '\n' + "답변 ";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Answer answer1 = (Answer) o;
        return Objects.equals(answerKey, answer1.answerKey) && Objects.equals(answer, answer1.answer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(answerKey, answer);
    }
}
