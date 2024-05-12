package app.habit.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "questions_id")
    private Long id;
    private String questionKey;
    private String question;

    @Column(name = "subject_id")
    private Long subjectId;

    public Question(String questionKey, String question) {
        this.questionKey = questionKey;
        this.question = question;
    }

    public void addSubjectId(long subjectId) {
        this.subjectId = subjectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Question question1 = (Question) o;
        return Objects.equals(questionKey, question1.questionKey) && Objects.equals(question,
                question1.question);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionKey, question);
    }
}
