package app.habit.domain;

import app.habit.service.gpt.request.RequestPrompt;
import org.springframework.stereotype.Component;

@Component
public class FeedbackPromptFactory {

    private static final String model = "gpt-3.5-turbo-0125";

    private static final String PREFIX = "";
    private static final String SUFFIX = "";

    public RequestPrompt create() {
        return new RequestPrompt();
    }
}
