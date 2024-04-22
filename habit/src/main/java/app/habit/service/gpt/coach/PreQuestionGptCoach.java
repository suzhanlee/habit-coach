package app.habit.service.gpt.coach;

import app.habit.dto.openaidto.GptRsWrapper;
import app.habit.dto.openaidto.GptRsWrapper.Choice.Message;
import app.habit.dto.openaidto.HabitPreQuestionRs;
import app.habit.service.gpt.request.RequestPrompt;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PreQuestionGptCoach {

    @Value("${app.api-key}")
    private String apiKey;

    private ExecutorService executorService = Executors.newFixedThreadPool(3);

    public CompletableFuture<List<HabitPreQuestionRs>> requestPreQuestions(RequestPrompt requestBody, String url) {
        System.out.println("PreQuestionGptCoach.requestPreQuestions1 : " + Thread.currentThread().getName());
        CompletableFuture<GptRsWrapper> adviceBody = writeAdvice(requestBody, url);
        System.out.println("PreQuestionGptCoach.requestPreQuestions2 : " + Thread.currentThread().getName());
        CompletableFuture<List<HabitPreQuestionRs>> listCompletableFuture = parseAdvice(adviceBody);
        System.out.println("PreQuestionGptCoach.requestPreQuestions3 : " + Thread.currentThread().getName());
        return listCompletableFuture;
    }

    public CompletableFuture<GptRsWrapper> writeAdvice(RequestPrompt requestBody, String url) {
        CompletableFuture<GptRsWrapper> future = WebClient.create()
                .post()
                .uri(url)
                .body(Mono.justOrEmpty(requestBody), RequestPrompt.class)
                .headers(httpHeaders -> httpHeaders.addAll(createHeaders()))
                .retrieve()
                .bodyToMono(GptRsWrapper.class)
                .toFuture();
        System.out.println("PreQuestionGptCoach.writeAdvice : " + Thread.currentThread().getName());
        return future;
    }


    private HttpHeaders createHeaders() {
        System.out.println("PreQuestionGptCoach.createHeaders : " + Thread.currentThread().getName());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    private CompletableFuture<List<HabitPreQuestionRs>> parseAdvice(CompletableFuture<GptRsWrapper> futureBody) {
        return futureBody.thenApplyAsync(gptRsWrapper -> {
            System.out.println("PreQuestionGptCoach.parseAdvice : " + Thread.currentThread().getName());
            Message message = gptRsWrapper.getChoices().get(0).getMessage();
            String content = message.getContent();
            TypeReference<List<HabitPreQuestionRs>> typeReference = new TypeReference<>() {};
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.readValue(content, typeReference);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
