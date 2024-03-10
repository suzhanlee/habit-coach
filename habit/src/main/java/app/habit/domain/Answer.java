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

    public String createPrompt() {
        return "Answer : " + this.answer + '\n';
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
