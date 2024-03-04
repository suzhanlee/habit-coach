package app.habit.domain;

import app.habit.dto.GptRsWrapper;
import app.habit.dto.GptRsWrapper.Choice.Message;
import app.habit.dto.HabitPreQuestionRs;
import app.habit.dto.PhaseEvaluationRs;
import app.habit.service.gpt.request.RequestPrompt;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
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
        GptRsWrapper advice = requestTotalAdvice(url, request);
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

    private GptRsWrapper requestTotalAdvice(String url, HttpEntity<RequestPrompt> request) {
        return restTemplate.exchange(url, HttpMethod.POST, request, GptRsWrapper.class).getBody();
    }

    private List<HabitPreQuestionRs> getCoreAdvice(GptRsWrapper body) {
        try {
            Message message = body.getChoices().get(0).getMessage();
            String content = message.getContent();
            TypeReference<List<HabitPreQuestionRs>> typeReference = new TypeReference<>() {};

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(content, typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
