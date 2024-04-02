package app.habit.dto.habitdto;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserHabitFeedbackDto {

    private Long feedbackModuleId;
    private String subject;
    private String feedback;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserHabitFeedbackDto that = (UserHabitFeedbackDto) o;
        return Objects.equals(feedbackModuleId, that.feedbackModuleId) && Objects.equals(subject,
                that.subject) && Objects.equals(feedback, that.feedback);
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedbackModuleId, subject, feedback);
    }
}
