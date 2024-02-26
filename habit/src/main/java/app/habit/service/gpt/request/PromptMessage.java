package app.habit.service.gpt.request;

import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PromptMessage {

    private String role;
    private String content;

    public PromptMessage(PromptRole role, String prompt) {
        this.role = role.getValue();
        this.content = prompt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PromptMessage that = (PromptMessage) o;
        return Objects.equals(role, that.role) && Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(role, content);
    }
}
