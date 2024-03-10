package app.habit.domain;

import app.habit.dto.GptRsWrapper;
import app.habit.dto.GptRsWrapper.Choice.Message;
import app.habit.dto.PhaseEvaluationRs;
import app.habit.dto.PhaseFeedbackRs;
import app.habit.service.gpt.request.RequestPrompt;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class FeedbackCoach {

    private static final String apiKey = "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";

    private final RestTemplate restTemplate;

    public PhaseFeedbackRs advice(RequestPrompt requestBody, String url) {
        HttpEntity<RequestPrompt> request = createAdviceRequest(requestBody);
        GptRsWrapper advice = requestTotalAdvice(url, request);
        return getCoreAdvice(advice);
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

    private PhaseFeedbackRs getCoreAdvice(GptRsWrapper body) {
        Message message = body.getChoices().get(0).getMessage();
        String content = message.getContent();
        TypeReference<PhaseFeedbackRs> typeReference = new TypeReference<>() {
        };
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(content, typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
