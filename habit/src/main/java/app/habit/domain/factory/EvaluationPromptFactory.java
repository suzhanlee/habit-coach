package app.habit.domain.factory;

import app.habit.dto.openaidto.SingleEvaluationPromptDto;
import app.habit.service.gpt.request.PromptMessage;
import app.habit.service.gpt.request.PromptRole;
import app.habit.service.gpt.request.RequestPrompt;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class EvaluationPromptFactory {

    private static final String model = "gpt-3.5-turbo-0125";

    private static final String PREFIX = """
            You are a habit formation expert.
            Below are user responses to the questionnaire you created to evaluate the habit formation stages of others.
            Please use the Transtheoretical Model (TTM) as an evaluation standard!
            You have an accurate understanding of the characteristics of each stage of TTM, and you can accurately evaluate the user's current habit formation stage by analyzing the user's answers from five perspectives.

            """;
    private static final String SUFFIX =
            """
                    Looking at the above, I would like you to tell me which stage the user is in according to the habit formation model and provide an explanation of that stage.
                    Evaluate the user’s habit formation stage in one of the five categories below!

                    1. Precontemplation stage
                    2. Consideration stage
                    3. Preparation stage
                    4.Action stage
                    5.Maintenance stage

                    The format of your answer is very important, so when you respond to me, please be sure to provide it in the same format as below!

                    {
                    "phaseType" : "consideration stage",
                    "phaseDescription" : "In the consideration phase of goal setting and planning, the individual is transitioning from simply thinking about making a change or developing a new habit to actively planning how to do it. This phase may involve vague ideas or It involves crystallizing your desires into specific, actionable goals – moving from “I want to make a difference” to “Here’s exactly what I’m going to do and how.” The focus is on specificity, feasibility, and the journey ahead. “There are plans for this.”"
                    }At this time, a very important point is that the values of phaseType and phaseDescription should be of String type.Please write the phase description in less than 300 characters""";

    public RequestPrompt create(List<SingleEvaluationPromptDto> totalEvaluationPromptDto) {
        String promptBuilder = PREFIX
                + createPrompt(totalEvaluationPromptDto)
                + SUFFIX;

        return new RequestPrompt(model, List.of(new PromptMessage(PromptRole.USER, promptBuilder)));
    }

    private String createPrompt(List<SingleEvaluationPromptDto> totalEvaluationPromptDto) {
        StringBuilder promptBuilder = new StringBuilder();

        for (SingleEvaluationPromptDto singleEvaluationPromptDto : totalEvaluationPromptDto) {
            String subjectKey = singleEvaluationPromptDto.getSubjectKey();
            String subjectContent = singleEvaluationPromptDto.getSubject();
            String questionContent = singleEvaluationPromptDto.getQuestion();
            String answerContent = singleEvaluationPromptDto.getAnswer();

            promptBuilder.append("## " + subjectKey + ". " + subjectContent + '\n'
                    + "Question : " + questionContent + '\n'
                    + "Answer : " + answerContent + '\n').append('\n');
        }

        return promptBuilder.toString();
    }
}
