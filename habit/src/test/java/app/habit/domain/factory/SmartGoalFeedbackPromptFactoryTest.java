package app.habit.domain.factory;

import static app.habit.domain.HabitFormingPhaseType.CONSIDERATION_STAGE;
import static org.assertj.core.api.Assertions.assertThat;

import app.habit.domain.HabitFormingPhaseType;
import app.habit.service.gpt.request.PromptMessage;
import app.habit.service.gpt.request.PromptRole;
import app.habit.service.gpt.request.RequestPrompt;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SmartGoalFeedbackPromptFactoryTest {

    @Test
    @DisplayName("사용자가 작성한 smart 목표에 대한 피드백을 요청하는 프롬프트를 생성한다.")
    void create_prompt() {
        // given
        SmartGoalFeedbackPromptFactory factory = new SmartGoalFeedbackPromptFactory();
        String habitName = "exercise";
        HabitFormingPhaseType habitFormingPhaseType = CONSIDERATION_STAGE;
        String smartGoal = "저는 일주일에 3번 3마일을 달리고 피트니스 앱으로 진행 상황을 추적할 것입니다. "
                + "매주 1마일 달리기로 시작하여 거리를 0.5마일씩 늘려 8주 이내에 3마일 목표를 달성할 것입니다.";

        // when
        RequestPrompt result = factory.create(habitName, habitFormingPhaseType, smartGoal);

        // then
        assertThat(result).isEqualTo(new RequestPrompt(
                "gpt-3.5-turbo-0125",
                List.of(new PromptMessage(PromptRole.USER,
                createExpectedPrompt()))));
    }

    private String createExpectedPrompt() {
        return """
           You are an expert in habit coaching!
           Users use a habit formation model called TTM.
           Stay in the action phase!
           This user has the following smart goals for the habits below and is in the habit formation stage below:
           habitName : exercise
           Habit Formation Stage : CONSIDERATION_STAGE
           smart goal : 저는 일주일에 3번 3마일을 달리고 피트니스 앱으로 진행 상황을 추적할 것입니다. 매주 1마일 달리기로 시작하여 거리를 0.5마일씩 늘려 8주 이내에 3마일 목표를 달성할 것입니다.
           I would like to provide answers to each part of s, m, a, r, and t to those who have the above habits and goals.
           Just provide feedback on smart goals right away without any additional words!
           """;
    }
}
