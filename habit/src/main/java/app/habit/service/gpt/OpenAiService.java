package app.habit.service.gpt;

import app.habit.domain.PreQuestionCoach;
import app.habit.domain.HabitAssessmentManager;
import app.habit.domain.HabitAssessmentManagerFactory;
import app.habit.domain.PromptFactory;
import app.habit.dto.HabitPreQuestionRs;
import app.habit.repository.HabitAssessmentManagerRepository;
import app.habit.service.gpt.request.RequestPrompt;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    private static final String url = "https://api.openai.com/v1/chat/completions";

    private final PreQuestionCoach preQuestionCoach;
    private final PromptFactory promptFactory;
    private final HabitAssessmentManagerFactory habitAssessmentManagerFactory;
    private final HabitAssessmentManagerRepository habitAssessmentManagerRepository;

    public List<HabitPreQuestionRs> getHabitPreQuestions(String type, String prompt) {
        RequestPrompt requestPrompt = promptFactory.createRequestBody(type, prompt);
        List<HabitPreQuestionRs> content = preQuestionCoach.advice(requestPrompt, url);
        HabitAssessmentManager habitAssessmentManager = habitAssessmentManagerFactory.createHabitAssessmentManager(content);
        habitAssessmentManagerRepository.save(habitAssessmentManager);
        return content;
    }
}
