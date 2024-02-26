package app.habit.dto;

import app.habit.service.gpt.PreQuestionDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GptRsWrapper<T> {

    private String id;
    private String object;
    private long created;
    private String model;
    private List<Choice<T>> choices;
    private Usage usage;
    private String system_fingerprint;

    public GptRsWrapper(List<Choice<T>> choices) {
        this.choices = choices;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Choice<T> {
        private long index;
        private Message<T> message;
        private String logprobs;
        private String finish_reason;

        public Choice(Message<T> message) {
            this.message = message;
        }

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Message<T> {
            private String role;
            @JsonDeserialize(using = PreQuestionDeserializer.class)
            private T content;

            public Message(T content) {
                this.content = content;
            }
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    static class Usage {
        private long prompt_tokens;
        private long completion_tokens;
        private long total_tokens;
    }
}
