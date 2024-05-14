package app.habit.domain.factory;

import static org.assertj.core.api.Assertions.assertThat;

import app.habit.service.gpt.request.PromptMessage;
import app.habit.service.gpt.request.PromptRole;
import app.habit.service.gpt.request.RequestPrompt;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SpecificPhasePreQuestionPromptFactoryTest {

    @Test
    @DisplayName("gpt에게 보낼 사용자의 습관 평가 단계를 향상 시키기 위한 사전 질문 프롬프트 dto를 생성한다.")
    void create_request_prompt() {
        // given
        SpecificPhasePreQuestionPromptFactory factory = new SpecificPhasePreQuestionPromptFactory();
        String givenPhaseType = "action stage";

        // when
        RequestPrompt result = factory.create(givenPhaseType);

        // then
        assertThat(result).isEqualTo(createExpected());
    }

    private RequestPrompt createExpected() {
        return new RequestPrompt("gpt-3.5-turbo-0125", List.of(new PromptMessage(PromptRole.USER,
                """
                        You are a habit formation expert.
                        Users can use the Transtheoretic Model (TTM)

                        1. Precontemplation stage
                        2. Consideration stage
                        3. Preparation stage
                        4. Action stage
                        5. Maintenance stage

                        Among the five stage
                                                
                        ACTION_STAGE It is located in .

                        You provide specific questions to the user's current habit-forming stage, and the user answers the questions
                        To help users strengthen their current habit-forming phase and move on to the next one
                        It will induce action.

                        An example is shown below.
                        The user is a consultation stage.

                        [
                        {
                        "feedbackModuleId" : 1,
                        "key" : 1,
                        "subject": "Questions on small actionable steps",
                        "questions": [
                        {
                        "questionKey" : "1",
                        "question" : "What are the first small steps you can take to achieve this goal?"
                        },
                        {
                        "questionKey" : "2",
                        "question" : "How do I integrate these little steps into my daily life?"
                        },
                        {
                        "questionKey" : "3",
                        "question" : "What do you want to gain by doing these little steps?"
                        }
                        ]
                        },
                        {
                        "feedbackModuleId" : 2,
                        "key" : 2,
                        "subject": "Questions about resources and support",
                        "questions": [
                        {
                        "questionKey" : "1",
                        "question" : "What are the resources needed to implement these small steps?"
                        },
                        {
                        "questionKey" : "2",
                        "question" : "What external support or resources can help you achieve this goal?"
                        },
                        {
                        "questionKey" : "3",
                        "question" : "What do you think it takes to keep these little steps going?"
                        }
                        ]
                        },
                        {
                        "feedbackModuleId" : 3,
                        "key" : 3,
                        "subject": "Initial success and motivation questions",
                        "questions": [
                        {
                        "questionKey" : "1",
                        "question" : "How do you plan to reward yourself or praise yourself when you've done these little steps successfully?"
                        },
                        {
                        "questionKey" : "2",
                        "question" : "What is your biggest motivation in taking small steps?"
                        },
                        {
                        "questionKey" : "3",
                        "question" : "How do I connect these small steps to other activities or habits in my daily life?"
                        }
                        ]
                        }
                        ]

                        You have to provide a response in the above format.
                        The field names feedbackModuleId, key, subject, questionKey, and question must be the same,
                        The number must also be the same.
                        However, the subject and the content of the questions should be different depending on the user's habit formation stage.

                        The important thing here is that you don't have to say anything else, you only have to give me the json-type response that I want
                        Also, there must be three blocks containing feedbackModuleId."""
        )))
                ;
    }
}
