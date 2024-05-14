package app.habit.dto.openaidto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FeedbackPromptDto {

    private String subject;
    private List<FeedbackDto> feedbackDtos;

    public FeedbackPromptDto(String subject) {
        this.subject = subject;
        this.feedbackDtos = new ArrayList<>();
    }

    public FeedbackPromptDto(String subject, List<FeedbackDto> feedbackDtos) {
        this.subject = subject;
        this.feedbackDtos = feedbackDtos;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeedbackDto {
        private String sessionKey;
        private String question;
        private String answer;
    }

    public void addFeedbackDto(String sessionKey, String question, String answer) {
        FeedbackDto feedbackDto = new FeedbackDto(sessionKey, question, answer);
        feedbackDtos.add(feedbackDto);
    }
}
