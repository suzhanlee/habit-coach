package app.habit.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GptRsWrapper {

    private String id;
    private String object;
    private long created;
    private String model;
    private List<Choice> choices;
    private Usage usage;
    private String system_fingerprint;

    public GptRsWrapper(List<Choice> choices) {
        this.choices = choices;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Choice {
        private long index;
        private Message message;
        private String logprobs;
        private String finish_reason;

        public Choice(Message message) {
            this.message = message;
        }

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Message {
            private String role;
            private String content;

            public Message (String content) {
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
