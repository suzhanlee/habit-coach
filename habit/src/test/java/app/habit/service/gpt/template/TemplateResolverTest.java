package app.habit.service.gpt.template;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TemplateResolverTest {

    private TemplateResolver templateResolver = new TemplateResolver(List.of(PromptTemplate.values()));

    @Test
    @DisplayName("타입에 따라 특정 템플릿을 반환한다.")
    void resolve_template() {
        // given
        String type = "사전 질문";

        // when
        PromptTemplate result = templateResolver.resolveTemplate(type);

        // then
        assertThat(result).isEqualTo(PromptTemplate.HABIT_PRE_QUESTION);
    }
}
