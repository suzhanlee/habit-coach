package app.habit.config;

import app.habit.domain.EvaluationCoach;
import app.habit.domain.EvaluationPromptFactory;
import app.habit.domain.PreQuestionCoach;
import app.habit.domain.HabitAssessmentManagerFactory;
import app.habit.domain.PromptFactory;
import app.habit.service.gpt.template.PromptTemplate;
import app.habit.service.gpt.template.TemplateResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Bean
    public EvaluationCoach evaluationCoach() {
        return new EvaluationCoach(restTemplate());
    }

    @Bean
    public EvaluationPromptFactory evaluationPromptFactory() {
        return new EvaluationPromptFactory();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
