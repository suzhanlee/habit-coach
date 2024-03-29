package app.habit.dto.habitdto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserHabitFeedbackModule {

    private UserHabitFeedbackDto userHabitFeedbackDto;
    private List<FeedbackSessionDto> feedbackSessionDtos;

    public UserHabitFeedbackModule(UserHabitFeedbackDto userHabitFeedbackDto, List<FeedbackSessionDto> feedbackSessionDtos) {
        this.userHabitFeedbackDto = userHabitFeedbackDto;
        this.feedbackSessionDtos = feedbackSessionDtos;
    }
}
