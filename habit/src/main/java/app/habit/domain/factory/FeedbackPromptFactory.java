package app.habit.domain.factory;

import app.habit.dto.openaidto.FeedbackPromptDto;
import app.habit.dto.openaidto.FeedbackPromptDto.FeedbackDto;
import app.habit.service.gpt.request.PromptMessage;
import app.habit.service.gpt.request.PromptRole;
import app.habit.service.gpt.request.RequestPrompt;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class FeedbackPromptFactory {

    private static final String model = "gpt-3.5-turbo-0125";

    private static final String PREFIX = """
            You are a habit formation expert.
            When you provide feedback, please consider the Transtheoretical Model (TTM) and answer it

            The person you need to give feedback to is in one of the five steps below.

            1. Precontemplation stage
            2. Consideration stage
            3. Preparation stage
            4. Action stage
            5. Maintenance stage

            Also, I need you to give me feedback by specializing in certain topics during this phase.

            The steps and topics to which the user belongs, and questions and answers are as follows.

            """;
    private static final String SUFFIX = """
            The question above is a question provided by you, a habit expert, to the user, and answer is based on the question by the user
            It's what I answered in my own situation. Through these questions and answers, you can help users develop their own habits
            You need to provide feedback to inspire action.
            The important point here is that you have to give a response in the format below.
            The Json format you will provide is as follows.

            {
            "feedbackSubject": "Set small steps to integrate into your daily life",
            "feedback" : "Stretching every morning is a good start. To incorporate this into your daily routine, make sure to prepare for the next day in the evening. This preparation will make stretching easier."
            }

            The field name 'feedbackSubject' and 'feedback' should be the same, and in the case of 'feedbackSubject' value, I deliver it
            The actual value of the field must be the same as the value given, but the actual value of the field considers the answer corresponding to the user's current stage, topic, and question
            You need to provide feedback.

            The important thing here is that you don't have to say anything else, you only have to give me the json-type response that I want""";

    public RequestPrompt create(FeedbackPromptDto feedbackPromptDto, String habitFormingPhaseType) {
        String promptBuilder = PREFIX
                + "habitFormingPhase : " + habitFormingPhaseType + '\n'
                + createPrompt(feedbackPromptDto)
                + SUFFIX;
        return new RequestPrompt(model, List.of(new PromptMessage(PromptRole.USER, promptBuilder)));
    }

    private String createPrompt(FeedbackPromptDto feedbackPromptDto) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("subject : ").append(feedbackPromptDto.getSubject()).append('\n');

        for (FeedbackDto feedbackDto : feedbackPromptDto.getFeedbackDtos()) {
            promptBuilder.append(
                    "number : " + feedbackDto.getSessionKey() + '\n'
                            + "question : " + feedbackDto.getQuestion() + '\n'
                            + "answer : " + feedbackDto.getAnswer() + '\n'
            );
        }

        return promptBuilder.toString();
    }
}
