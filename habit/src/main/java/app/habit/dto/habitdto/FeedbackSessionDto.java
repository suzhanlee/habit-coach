package app.habit.dto.habitdto;

import app.habit.domain.FeedbackSession;
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
}
