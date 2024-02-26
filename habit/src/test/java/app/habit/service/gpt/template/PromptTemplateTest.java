package app.habit.service.gpt.template;

import static app.habit.service.gpt.prompt.PromptExample.HABIT_PRE_QUESTION_PROMPT;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PromptTemplateTest {

    @Test
    @DisplayName("처리할 수 있는 프롬프트 타입인지 확인한다.")
    void supports() {
        // given
        String type = "사전 질문";

        // when
        PromptTemplate template = PromptTemplate.HABIT_PRE_QUESTION;
        boolean result = template.supports(type);

        // then
        assertThat(result).isEqualTo(true);
    }

    @Test
    @DisplayName("해당 프롬프트 타입은 처리할 수 없다.")
    void not_support() {
        // given
        String type = "평가 질문";

        // when
        PromptTemplate template = PromptTemplate.HABIT_PRE_QUESTION;
        boolean result = template.supports(type);

        // then
        assertThat(result).isEqualTo(false);
    }

    @Test
    @DisplayName("템플릿으로 프롬프트를 생성한다.")
    void create_prompt_with_template() {
        // given
        String userPrompt = "";

        // when
        PromptTemplate template = PromptTemplate.HABIT_PRE_QUESTION;
        String result = template.createPromptWith(userPrompt);

        // then
        assertThat(result).isEqualTo(HABIT_PRE_QUESTION_PROMPT);
    }
}
