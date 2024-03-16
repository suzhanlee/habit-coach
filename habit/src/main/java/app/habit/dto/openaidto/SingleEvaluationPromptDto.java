package app.habit.dto.openaidto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SingleEvaluationPromptDto {

    private String subjectKey;
    private String subject;
    private String question;
    private String answer;
}
