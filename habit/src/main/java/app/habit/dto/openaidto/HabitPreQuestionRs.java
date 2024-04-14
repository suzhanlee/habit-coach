package app.habit.dto.openaidto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HabitPreQuestionRs {

    private String key;
    private String subject;
    private QuestionRs questionRs;
}
