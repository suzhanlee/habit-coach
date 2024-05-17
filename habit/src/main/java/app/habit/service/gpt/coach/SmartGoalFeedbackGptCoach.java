package app.habit.service.gpt.coach;

import app.habit.dto.openaidto.GptRsWrapper;
import app.habit.dto.openaidto.GptRsWrapper.Choice.Message;
import app.habit.service.gpt.request.RequestPrompt;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SmartGoalFeedbackGptCoach {

    private final WebClient webClient;

    @Value("${app.api-key}")
    private String apiKey;

    public CompletableFuture<String> requestSmartGoalFeedback(RequestPrompt requestBody, String url) {
        CompletableFuture<GptRsWrapper> adviceBody = writeFeedback(requestBody, url);
        return parseFeedback(adviceBody);
    }

    private CompletableFuture<GptRsWrapper> writeFeedback(RequestPrompt requestBody, String url) {
        return webClient
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

    private CompletableFuture<String> parseFeedback(CompletableFuture<GptRsWrapper> futureBody) {
        return futureBody.thenApplyAsync(gptRsWrapper -> {
            Message message = gptRsWrapper.getChoices().get(0).getMessage();
            return message.getContent();
        });
    }
}
