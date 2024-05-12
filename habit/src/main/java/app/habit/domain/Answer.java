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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long id;
    private String answerKey;
    private String answer;

    @Column(name = "subject_id")
    private Long subjectId;

    public Answer(String answerKey, String answer) {
        this.answerKey = answerKey;
        this.answer = answer;
    }

    public void addSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }
}
