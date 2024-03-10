package app.habit.domain;

import app.habit.service.gpt.request.PromptMessage;
import app.habit.service.gpt.request.PromptRole;
import app.habit.service.gpt.request.RequestPrompt;
import app.habit.service.gpt.template.TemplateResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PromptFactory {

    private static final String model = "gpt-3.5-turbo-0125";

    private final TemplateResolver templateResolver;

    public RequestPrompt createRequestBody(String type, String userPrompt) {
        String prompt = createPrompt(type, userPrompt);
        return new RequestPrompt(model, List.of(new PromptMessage(PromptRole.USER, prompt)));
    }

    private String createPrompt(String type, String userPrompt) {
        return templateResolver.resolveTemplate(type).createPromptWith(userPrompt);
    }
}
