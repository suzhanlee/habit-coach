package app.habit.service.gpt.coach;

import static org.assertj.core.api.Assertions.assertThat;

import app.habit.dto.openaidto.PhaseEvaluationRs;
import app.habit.dto.openaidto.PhaseFeedbackRs;
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
public class FeedbackGptCoachTest {

    @Autowired
    private FeedbackGptCoach feedbackGptCoach;
    private final String testUrl = "https://api.openai.com/v1/chat/completions";

    @Test
    @DisplayName("gpt 에게 프롬프트를 보내 사용자의 현재 습관 형성 단계 발전을 위한 질문 대답에 대한 피드백을 받아온다.")
    void send_prompt_to_gpt() {
        // given
        String prompt = """
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

                                habitFormingPhase : Preparation stage
                                subject : 실천 가능한 작은 단계에 대한 질문들
                                number : 1
                                question : 이 목표를 달성하기 위해 가장 먼저 시작할 수 있는 작은 단계는 무엇인가요?
                                answer : 가장 먼저, 매일 아침 10분간 스트레칭을 시작할 계획입니다.
                                number : 2
                                question : 이 작은 단계들을 어떻게 일상에 통합할 수 있을까요?
                                answer : 이를 위해, 매일 아침 알람을 설정하고, 침대 옆에 스트레칭 매트를 놓을 것입니다.
                                number : 3
                                question : 이 작은 단계들을 수행함으로써 얻고자 하는 바는 무엇인가요?
                                answer : 이 작은 습관을 통해 하루를 더 활기차게 시작하고, 유연성을 향상시키고 싶습니다.
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


        RequestPrompt requestPrompt = new RequestPrompt("gpt-3.5-turbo-0125",
                List.of(new PromptMessage(PromptRole.USER, prompt)));

        // when
        CompletableFuture<PhaseFeedbackRs> result = feedbackGptCoach.requestPhaseFeedback(
                requestPrompt,
                testUrl);

        // then
        StepVerifier.create(Mono.fromFuture(result))
                .expectSubscription()
                .assertNext(phaseFeedbackRs -> {
                    assertThat(phaseFeedbackRs).isNotNull();
                    assertThat(phaseFeedbackRs.getFeedbackSubject()).isNotNull();
                    assertThat(phaseFeedbackRs.getFeedback()).isNotNull();
                })
                .verifyComplete();
    }
}
