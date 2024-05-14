package app.habit.domain.factory;

import static org.assertj.core.api.Assertions.assertThat;

import app.habit.dto.openaidto.FeedbackPromptDto;
import app.habit.dto.openaidto.FeedbackPromptDto.FeedbackDto;
import app.habit.service.gpt.request.PromptMessage;
import app.habit.service.gpt.request.PromptRole;
import app.habit.service.gpt.request.RequestPrompt;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FeedbackPromptFactoryTest {

    @Test
    @DisplayName("gpt에게 보낼 프롬프트 dto를 생성한다.")
    void create_request_prompt() {
        // given
        FeedbackPromptFactory factory = new FeedbackPromptFactory();
        String givenHabitFormingPhaseType = "Consideration stage";

        // when
        RequestPrompt result = factory.create(createFeedbackDto(), givenHabitFormingPhaseType);

        // then
        assertThat(result).isEqualTo(createExpected());
    }

    private FeedbackPromptDto createFeedbackDto() {
        FeedbackPromptDto feedbackPromptDto = new FeedbackPromptDto("주제1", List.of(
                new FeedbackDto("1", "질문1", "대답1"),
                new FeedbackDto("2", "질문2", "대답2"),
                new FeedbackDto("3", "질문3", "대답3")
        ));
        return feedbackPromptDto;
    }

    private RequestPrompt createExpected() {
        return new RequestPrompt("gpt-3.5-turbo-0125", List.of(new PromptMessage(PromptRole.USER, """
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

                habitFormingPhase : Consideration stage
                subject : 주제1
                number : 1
                question : 질문1
                answer : 대답1
                number : 2
                question : 질문2
                answer : 대답2
                number : 3
                question : 질문3
                answer : 대답3
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

                The important thing here is that you don't have to say anything else, you only have to give me the json-type response that I want""")));
    }
}
