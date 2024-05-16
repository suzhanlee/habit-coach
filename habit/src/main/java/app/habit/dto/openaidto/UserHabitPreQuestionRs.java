package app.habit.dto.openaidto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserHabitPreQuestionRs {

    private long feedbackModuleId;
    private String key;
    private String subject;
    private List<PhaseQuestionRs> questions;

    public UserHabitPreQuestionRs(String key, String subject, List<PhaseQuestionRs> questions) {
        this.key = key;
        this.subject = subject;
        this.questions = questions;
    }
}
