package app.habit.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QuestionTest {

    @Test
    @DisplayName("question으로 필요한 프롬프트 조각을 만든다.")
    void create_prompt_in_question_section() {
        // given
        Question given = new Question("1", "질문1");

        // when
        String result = given.createPrompt();

        // then
        assertThat(result).isEqualTo("Question : 질문1\n");
    }
}
