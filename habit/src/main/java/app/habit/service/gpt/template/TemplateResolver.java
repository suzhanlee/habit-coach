package app.habit.service.gpt.template;

import java.util.List;

public class TemplateResolver {

    private final List<PromptTemplate> promptTemplates;

    public TemplateResolver(List<PromptTemplate> promptTemplates) {
        this.promptTemplates = promptTemplates;
    }

    public PromptTemplate resolveTemplate(String type) {
        return promptTemplates.stream()
                .filter(p -> p.supports(type))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }
}
