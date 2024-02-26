package app.habit.config;

import app.habit.domain.PromptFactory;
import app.habit.service.gpt.template.PromptTemplate;
import app.habit.service.gpt.template.TemplateResolver;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public TemplateResolver templateResolver() {
        return new TemplateResolver(List.of(PromptTemplate.values()));
    }

    @Bean
    public PromptFactory promptFactory() {
        return new PromptFactory(templateResolver());
    }
}
