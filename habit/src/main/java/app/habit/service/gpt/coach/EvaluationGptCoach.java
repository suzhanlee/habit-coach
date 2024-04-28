package app.habit.service.gpt.coach;

import app.habit.dto.openaidto.GptRsWrapper;
import app.habit.dto.openaidto.GptRsWrapper.Choice.Message;
import app.habit.dto.openaidto.PhaseEvaluationRs;
import app.habit.service.gpt.request.RequestPrompt;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class EvaluationGptCoach {

    @Value("${app.api-key}")
    private String apiKey;

    public CompletableFuture<PhaseEvaluationRs> requestEvaluation(RequestPrompt requestBody, String url) {
        log.info("EvaluationGptCoach.requestEvaluation : " + Thread.currentThread().getName());
        CompletableFuture<GptRsWrapper> adviceBody = writeAdvice(requestBody, url);
        return parseAdvice(adviceBody);
    }

    private CompletableFuture<GptRsWrapper> writeAdvice(RequestPrompt requestBody, String url) {
        return WebClient.create()
                .post()
                .uri(url)
                .body(Mono.justOrEmpty(requestBody), RequestPrompt.class)
                .headers(httpHeaders -> httpHeaders.addAll(createHeaders()))
                .retrieve()
                .bodyToMono(GptRsWrapper.class)
                .toFuture();
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    private CompletableFuture<PhaseEvaluationRs> parseAdvice(CompletableFuture<GptRsWrapper> futureBody) {
        return futureBody.thenApplyAsync(gptRsWrapper -> {
            log.info("EvaluationGptCoach.parseAdvice : " + Thread.currentThread().getName());
            Message message = gptRsWrapper.getChoices().get(0).getMessage();
            String content = message.getContent();
            TypeReference<PhaseEvaluationRs> typeReference = new TypeReference<>() {
            };
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.readValue(content, typeReference);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
