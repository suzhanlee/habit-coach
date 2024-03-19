package app.habit.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AnswerTest {

    @Test
    @DisplayName("answer로 필요한 프롬프트 조각을 만든다.")
    void create_prompt_in_answer_section() {
        // given
        Answer given = new Answer("1", "대답1");

        // when
        String result = given.createPrompt();

        // then
        assertThat(result).isEqualTo("Answer : 대답1\n");
    }
}
