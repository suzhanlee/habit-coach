package app.habit.service.gpt.coach;

import static org.assertj.core.api.Assertions.assertThat;

import app.habit.dto.openaidto.UserHabitPreQuestionRs;
import app.habit.service.gpt.request.PromptMessage;
import app.habit.service.gpt.request.PromptRole;
import app.habit.service.gpt.request.RequestPrompt;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
public class PhaseGptCoachTest {

    @Autowired
    private PhaseGptCoach phaseGptCoach;
    private final String testUrl = "https://api.openai.com/v1/chat/completions";

    @Test
    @DisplayName("gpt 에게 프롬프트를 보내 사용자의 현재 습관 형성 단계 발전을 위한 사전 질문지를 받아온다.")
    void send_prompt_to_gpt() {
        // given
        String prompt = """
            You are a habit formation expert.
            Users can use the Transtheoretic Model (TTM)

            1. Precontemplation stage
            2. Consideration stage
            3. Preparation stage
            4. Action stage
            5. Maintenance stage

            Among the five stage
            
            consideration stage
            
            It is located in .

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
            Also, there must be three blocks containing feedbackModuleId.""";

        RequestPrompt requestPrompt = new RequestPrompt("gpt-3.5-turbo-0125",
                List.of(new PromptMessage(PromptRole.USER, prompt)));

        // when
        CompletableFuture<List<UserHabitPreQuestionRs>> result = phaseGptCoach.requestUserHabitPreQuestions(
                requestPrompt,
                testUrl);

        // then
        StepVerifier.create(Mono.fromFuture(result))
                .expectSubscription()
                .assertNext(userHabitPreQuestionRsList -> {
                    assertThat(userHabitPreQuestionRsList).isNotNull();
                    assertThat(userHabitPreQuestionRsList).isNotEmpty();

                    assertUserHabitPreQuestionRsList(userHabitPreQuestionRsList, 0);
                    assertUserHabitPreQuestionRsList(userHabitPreQuestionRsList, 1);
                    assertUserHabitPreQuestionRsList(userHabitPreQuestionRsList, 2);
                })
                .verifyComplete();
    }

    private void assertUserHabitPreQuestionRsList(List<UserHabitPreQuestionRs> userHabitPreQuestionRsList, int index) {
        assertThat(userHabitPreQuestionRsList.get(index).getFeedbackModuleId()).isNotNull();
        assertThat(userHabitPreQuestionRsList.get(index).getKey()).isNotNull();
        assertThat(userHabitPreQuestionRsList.get(index).getSubject()).isNotNull();
        assertThat(userHabitPreQuestionRsList.get(index).getQuestions()).isNotNull();
        assertThat(userHabitPreQuestionRsList.get(index).getQuestions()).isNotEmpty();
    }
}
