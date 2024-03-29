package app.habit.dto.habitdto;

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
}
