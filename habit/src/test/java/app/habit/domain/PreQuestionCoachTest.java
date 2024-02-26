package app.habit.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import app.habit.dto.GptRsWrapper;
import app.habit.dto.GptRsWrapper.Choice;
import app.habit.dto.GptRsWrapper.Choice.Message;
import app.habit.dto.HabitPreQuestionRs;
import app.habit.service.gpt.request.RequestPrompt;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class PreQuestionCoachTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PreQuestionCoach preQuestionCoach;

    @Test
    @DisplayName("사전 질문 전담 코치에게 조언을 구하는지 확인한다.")
    void request_to_gpt_for_pre_question() {
        // given
        RequestPrompt givenMockRq = new RequestPrompt();
        String givenUrl = "https://api.openai.com/v1/chat/completions";

        List<HabitPreQuestionRs> actualMockContent = createContent();
        GptRsWrapper<List<HabitPreQuestionRs>> mockRs = createMockRs(actualMockContent);

        // when
        when(restTemplate.exchange(
                eq(givenUrl),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(new ParameterizedTypeReference<GptRsWrapper<List<HabitPreQuestionRs>>>() {
                })
        )).thenReturn(new ResponseEntity<>(mockRs, HttpStatus.OK));

        List<HabitPreQuestionRs> result = preQuestionCoach.advice(givenMockRq, givenUrl);

        // then
        assertThat(result.size()).isEqualTo(actualMockContent.size());
    }

    private List<HabitPreQuestionRs> createContent() {
        return List.of(new HabitPreQuestionRs());
    }

    private GptRsWrapper<List<HabitPreQuestionRs>> createMockRs(
            List<HabitPreQuestionRs> actualMockContent) {
        return new GptRsWrapper<>(List.of(
                new Choice<List<HabitPreQuestionRs>>(new Message<List<HabitPreQuestionRs>>(actualMockContent))));
    }
}
