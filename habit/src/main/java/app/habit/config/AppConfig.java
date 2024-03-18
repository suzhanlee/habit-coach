package app.habit.config;

import app.habit.domain.PreQuestionCoach;
import app.habit.domain.HabitAssessmentManagerFactory;
import app.habit.domain.PromptFactory;
import app.habit.service.gpt.template.PromptTemplate;
import app.habit.service.gpt.template.TemplateResolver;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public TemplateResolver templateResolver() {
        return new TemplateResolver(List.of(PromptTemplate.values()));
    }

    @Bean
    public PromptFactory promptFactory() {
        return new PromptFactory(templateResolver());
    }

    @Bean
    public PreQuestionCoach coach() {
        return new PreQuestionCoach(restTemplate());
    }

    @Bean
    public HabitAssessmentManagerFactory habitAssessmentManagerFactory() {
        return new HabitAssessmentManagerFactory();
    }
}
