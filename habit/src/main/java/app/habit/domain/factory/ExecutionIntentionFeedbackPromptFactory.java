package app.habit.domain.factory;

import app.habit.domain.HabitFormingPhaseType;
import app.habit.service.gpt.request.PromptMessage;
import app.habit.service.gpt.request.PromptRole;
import app.habit.service.gpt.request.RequestPrompt;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ExecutionIntentionFeedbackPromptFactory {

    private static final String model = "gpt-3.5-turbo-0125";

    private static final String PREFIX = """
            You are an expert in habit coaching!
            Users use a habit formation model called TTM.
            Stay in the action phase!
            This user has the following execution intention for the habits below and is in the habit formation stage below:
            """;

    private static final String SUFFIX = "I would like to provide feedback on the implementation intention statement right away without any additional words!";

    public RequestPrompt create(String habitName, HabitFormingPhaseType habitFormingPhaseType,
                                String executionIntention) {
        String promptBuilder = PREFIX
                + "habitName : " + habitName + '\n'
                + "Habit Formation Stage : " + habitFormingPhaseType + '\n'
                + "execution intention : " + executionIntention + '\n'
                + SUFFIX;
        return new RequestPrompt(model, List.of(new PromptMessage(PromptRole.USER, promptBuilder)));
    }
}
