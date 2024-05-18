package app.habit.domain.factory;

import static app.habit.domain.HabitFormingPhaseType.ACTION_STAGE;
import static app.habit.domain.HabitFormingPhaseType.CONSIDERATION_STAGE;
import static org.assertj.core.api.Assertions.assertThat;

import app.habit.domain.HabitFormingPhaseType;
import app.habit.service.gpt.request.PromptMessage;
import app.habit.service.gpt.request.PromptRole;
import app.habit.service.gpt.request.RequestPrompt;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExecutionIntentionFeedbackPromptFactoryTest {

    @Test
    @DisplayName("사용자가 작성한 실행 의도에 대한 피드백을 요청하는 프롬프트를 생성한다.")
    void create_prompt() {
        // given
        ExecutionIntentionFeedbackPromptFactory factory = new ExecutionIntentionFeedbackPromptFactory();
        String habitName = "reading";
        HabitFormingPhaseType habitFormingPhaseType = ACTION_STAGE;
        String executionIntention = "매일 30분 동안 책을 읽고, 독서 일지에 읽은 내용을 기록할 것입니다. 매일 밤 10시에 읽기 시작하여 잠들기 전에 마칠 계획입니다.";

        // when
        RequestPrompt result = factory.create(habitName, habitFormingPhaseType, executionIntention);

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
                This user has the following execution intention for the habits below and is in the habit formation stage below:
                habitName : reading
                Habit Formation Stage : ACTION_STAGE
                execution intention : 매일 30분 동안 책을 읽고, 독서 일지에 읽은 내용을 기록할 것입니다. 매일 밤 10시에 읽기 시작하여 잠들기 전에 마칠 계획입니다.
                I would like to provide feedback on the implementation intention statement right away without any additional words!""";
    }
}
