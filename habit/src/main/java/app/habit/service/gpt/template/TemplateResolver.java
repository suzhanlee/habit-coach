package app.habit.service.gpt.template;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TemplateResolver {

    private final List<PromptTemplate> promptTemplates;

    @Autowired
    public TemplateResolver(List<PromptTemplate> promptTemplates) {
        this.promptTemplates = List.of(PromptTemplate.values());
    }

    public PromptTemplate resolveTemplate(String type) {
        return promptTemplates.stream()
                .filter(p -> p.supports(type))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }
}
