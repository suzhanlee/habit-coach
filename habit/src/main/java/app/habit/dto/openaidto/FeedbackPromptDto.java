package app.habit.dto.openaidto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            FeedbackDto that = (FeedbackDto) o;
            return Objects.equals(sessionKey, that.sessionKey) && Objects.equals(question, that.question)
                    && Objects.equals(answer, that.answer);
        }

        @Override
        public int hashCode() {
            return Objects.hash(sessionKey, question, answer);
        }
    }

    public void addFeedbackDto(String sessionKey, String question, String answer) {
        FeedbackDto feedbackDto = new FeedbackDto(sessionKey, question, answer);
        feedbackDtos.add(feedbackDto);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FeedbackPromptDto that = (FeedbackPromptDto) o;
        return Objects.equals(subject, that.subject) && Objects.equals(feedbackDtos, that.feedbackDtos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subject, feedbackDtos);
    }
}
