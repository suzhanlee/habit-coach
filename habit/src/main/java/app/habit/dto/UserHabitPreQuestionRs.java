package app.habit.dto;

import app.habit.domain.FeedbackModule;
import app.habit.domain.FeedbackSession;
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
    private List<PhaseQuestionRs> phaseQuestions;

    public FeedbackModule toFeedbackModule() {
        FeedbackModule feedbackModule = new FeedbackModule(key, subject);
        for (PhaseQuestionRs phaseQuestion : this.phaseQuestions) {
            feedbackModule.addSession(new FeedbackSession(phaseQuestion.getKey(), phaseQuestion.getQuestion()));
        }
        return feedbackModule;
    }
}
