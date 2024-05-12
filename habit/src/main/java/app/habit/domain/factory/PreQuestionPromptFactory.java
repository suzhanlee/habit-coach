package app.habit.domain.factory;

import app.habit.service.gpt.request.PromptMessage;
import app.habit.service.gpt.request.PromptRole;
import app.habit.service.gpt.request.RequestPrompt;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PreQuestionPromptFactory {

    private static final String model = "gpt-3.5-turbo-0125";

    public static final String PROMPT = """
            SITUATION
            Many people strive to develop new habits, but often struggle to track their progress and ensure their efforts align with broader goals.
            Recognizing and addressing these issues is critical to successful habit formation.

            TASK
            Create a structured series of questions to evaluate a person's progress in forming new habits.
            These questions should address five key perspectives below:
            1. General perception and intention
            2. Goal clarity and planning
            3. Initial actions and small steps
            4. Consistency and practice
            5. Integration of maintenance and lifestyle

            INTENT
            The purpose of these questions is to help individuals reflect on their journey toward habit formation, identify areas where they may need to adjust their approach, and commit to incorporating these habits into their lifestyle.

            CONCERNS
            One potential problem is that the questions are comprehensive yet simple enough for individuals to understand and apply them to their own context.
            It is also important to encourage honest self-reflection without becoming discouraged.

            CALIBRATION
            You must reply me in JSON format.
            When providing a response in JSON format, the following information must be observed. That's the most important part.
            1. Be sure to follow the JSON structure below.
            In particular, the object structure below and the fact that one question must be asked from each of the five perspectives must be perfectly observed.
            2. The values \u200B\u200Bof key, questionKey, and question may be different.
            In particular, it would be nice if questions were asked more specifically in the example below to encourage self-reflection in users!
            3. When responding to me, translate the English response into Korean.
            4. You will probably put the response in the below json format in the String type content field,
            This is very important at this time, and I would like you to provide the following structure in the same Json structure, not the String type
            5. If you think the response will exceed 4096 tokens while re-watching the response, process the response and deliver it to me so that it doesn't exceed 4096 tokens. Of course, the final response to me should be in the same format as previously mentioned
            A very easy way to follow the above method is to remove the outermost commas of the content field of the message object of the response you will create.

            JSON structure example

            [
            {
            "key" : "1",
            "subject" : "General perception and intention",
            "questionRs" : {
            "questionKey" : "1",
            "question" : "What habits are you currently trying to develop, and why do you think they are important?"
            }
            },
            {
            "key": "2",
            "subject" : "Goal clarity and planning",
            "questionRs" : {
            "questionKey" : "2",
            "question" : "Have you set specific goals related to this habit? What steps have you planned to achieve these goals?"
            }
            },
            {
            "key" : "3",
            "subject" : "Initial actions and small steps",
            "questionRs" : {
            "questionKey" : "3",
            "question" : "What is the smallest step you have taken or plan to take to start this habit?"
            }
            },
            {
            "key" : "4",
            "subject" : "Consistency and practice",
            "questionRs" : {
            "questionKey" : "4",
            "question" : "How consistently have you been able to perform this habit and how do you monitor your progress?"
            }
            },
            {
            "key" : "5",
            "subject" : "Maintenance and lifestyle integration",
            "questionRs" : {
            "questionKey" : "5",
            "question" : "Have you ever had trouble maintaining these habits over time? How have you dealt with them?"
            }
            }
            ]""";

    public RequestPrompt create() {
        return new RequestPrompt(model, List.of(new PromptMessage(PromptRole.USER, PROMPT)));
    }
}
