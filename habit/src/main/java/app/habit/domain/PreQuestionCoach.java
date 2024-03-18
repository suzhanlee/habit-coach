package app.habit.domain;

import app.habit.dto.GptRsWrapper;
import app.habit.dto.HabitPreQuestionRs;
import app.habit.service.gpt.request.RequestPrompt;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
public class PreQuestionCoach {

    private static final String apiKey = "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";

    private final RestTemplate restTemplate;

    public List<HabitPreQuestionRs> advice(RequestPrompt requestBody, String url) {
        HttpEntity<RequestPrompt> request = createAdviceRequest(requestBody);
        GptRsWrapper<List<HabitPreQuestionRs>> advice = requestTotalAdvice(url, request);
        return getCoreAdvice(Objects.requireNonNull(advice));
    }

    private HttpEntity<RequestPrompt> createAdviceRequest(RequestPrompt requestBody) {
        return new HttpEntity<>(requestBody, createHeaders());
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    private GptRsWrapper<List<HabitPreQuestionRs>> requestTotalAdvice(String url, HttpEntity<RequestPrompt> request) {
        return restTemplate.exchange(url, HttpMethod.POST, request, createAdviceType()).getBody();
    }

    private ParameterizedTypeReference<GptRsWrapper<List<HabitPreQuestionRs>>> createAdviceType() {
        return new ParameterizedTypeReference<GptRsWrapper<List<HabitPreQuestionRs>>>() {
        };
    }

    private List<HabitPreQuestionRs> getCoreAdvice(GptRsWrapper<List<HabitPreQuestionRs>> body) {
        return body.getChoices().get(0).getMessage().getContent();
    }
}
