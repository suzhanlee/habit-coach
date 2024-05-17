package app.habit.service.gpt.coach;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import app.habit.dto.openaidto.HabitPreQuestionRs;
import app.habit.service.gpt.request.PromptMessage;
import app.habit.service.gpt.request.PromptRole;
import app.habit.service.gpt.request.RequestPrompt;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
public class SmartGoalFeedbackGptCoachTest {

    @Autowired
    private SmartGoalFeedbackGptCoach smartGoalFeedbackGptCoach;
    private final String testUrl = "https://api.openai.com/v1/chat/completions";

    @Test
    @DisplayName("gpt 에게 프롬프트를 보내 사용자 습관 형성 단계 평가를 위한 사전 질문지를 받아온다.")
    void send_prompt_to_gpt() {
        // given
        String prompt = """
           You are an expert in habit coaching!
           Users use a habit formation model called TTM.
           Stay in the action phase!
           This user has the following smart goals for the habits below and is in the habit formation stage below:
           habitName : exercise
           Habit Formation Stage : CONSIDERATION_STAGE
           smart goal : 저는 일주일에 3번 3마일을 달리고 피트니스 앱으로 진행 상황을 추적할 것입니다. 매주 1마일 달리기로 시작하여 거리를 0.5마일씩 늘려 8주 이내에 3마일 목표를 달성할 것입니다.
           I would like to provide answers to each part of s, m, a, r, and t to those who have the above habits and goals.
           Just provide feedback on smart goals right away without any additional words!
           """;;
        RequestPrompt requestPrompt = new RequestPrompt("gpt-3.5-turbo-0125",
                List.of(new PromptMessage(PromptRole.USER, prompt)));

        // when
        CompletableFuture<String> result = smartGoalFeedbackGptCoach.requestSmartGoalFeedback(
                requestPrompt,
                testUrl);

        // then
        StepVerifier.create(Mono.fromFuture(result))
                .expectSubscription()
                .assertNext(string -> {
                    assertThat(string).isNotNull();
                    assertThat(string).isNotEmpty();
                    assertThat(string).isNotBlank();
                })
                .verifyComplete();
    }
}
