package app.habit.dto.openaidto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PhaseFeedbackRs {

    private String feedbackSubject;
    private String feedback;
}
