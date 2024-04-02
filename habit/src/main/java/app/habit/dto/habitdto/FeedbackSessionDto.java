package app.habit.dto.habitdto;

import app.habit.domain.FeedbackSession;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackSessionDto {

    private String question;
    private String answer;

    public FeedbackSessionDto(FeedbackSession feedbackSession) {
        this.question = feedbackSession.getQuestion();
        this.answer = feedbackSession.getAnswer();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FeedbackSessionDto that = (FeedbackSessionDto) o;
        return Objects.equals(question, that.question) && Objects.equals(answer, that.answer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(question, answer);
    }
}
