package app.habit.domain;

import static app.habit.service.gpt.prompt.PromptExample.HABIT_PRE_QUESTION_PROMPT;
import static org.assertj.core.api.Assertions.assertThat;

import app.habit.service.gpt.request.PromptMessage;
import app.habit.service.gpt.request.PromptRole;
import app.habit.service.gpt.request.RequestPrompt;
import app.habit.service.gpt.template.PromptTemplate;
import app.habit.service.gpt.template.TemplateResolver;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PromptFactoryTest {

    private PromptFactory promptFactory = new PromptFactory(new TemplateResolver(List.of(PromptTemplate.values())));

    @Test
    @DisplayName("사용자 프롬프트를 gpt에게 보낼 프롬프트로 규격화한다.")
    void create_prompt_with_user_prompt() {
        // given
        String type = "사전 질문";
        String userPrompt = "";

        // when
        RequestPrompt result = promptFactory.createRequestBody(type, userPrompt);

        // then
        assertThat(result).isEqualTo(new RequestPrompt("gpt-3.5-turbo-0125", List.of(new PromptMessage(PromptRole.USER, HABIT_PRE_QUESTION_PROMPT))));
    }
}
