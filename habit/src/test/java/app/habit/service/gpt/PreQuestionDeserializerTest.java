package app.habit.service.gpt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import app.habit.dto.HabitPreQuestionRs;
import app.habit.dto.QuestionRs;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PreQuestionDeserializerTest {

    @Mock
    private JsonParser jsonParser;

    @Mock
    private DeserializationContext ctxt;

    @Test
    @DisplayName("gpt의 사전 질문 응답을 파싱한다.")
    void deserialize_pre_question_response() throws IOException {
        // given
        String given = createGivenJson();

        // when
        when(jsonParser.getText()).thenReturn(given);

        PreQuestionDeserializer deserializer = new PreQuestionDeserializer();
        List<HabitPreQuestionRs> result = deserializer.deserialize(jsonParser, ctxt);

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(createExpected());
    }

    private String createGivenJson() {
        return "[\n"
                + "{\n"
                + "\"key\": \"1\",\n"
                + "\"subject\": \"일반적 인식과 의도\",\n"
                + "\"questionRs\": {\n"
                + "\"questionKey\": \"1\",\n"
                + "\"question\": \"현재 무엇을 개발하려고 노력하고 있으며, 해당 습관이 왜 중요한지 생각하십니까?\"\n"
                + "}\n"
                + "},\n"
                + "{\n"
                + "\"key\": \"2\",\n"
                + "\"subject\": \"목표 명확성과 계획\",\n"
                + "\"questionRs\": {\n"
                + "\"questionKey\": \"2\",\n"
                + "\"question\": \"이 습관과 관련된 구체적인 목표를 설정했습니까? 이러한 목표를 달성하기 위해 어떤 계획을 세우셨나요?\"\n"
                + "}\n"
                + "},\n"
                + "{\n"
                + "\"key\": \"3\",\n"
                + "\"subject\": \"초기 조치 및 소규모 단계\",\n"
                + "\"questionRs\": {\n"
                + "\"questionKey\": \"3\",\n"
                + "\"question\": \"이 습관을 시작하려면 어떤 가장 작은 단계를 취했거나 계획하셨나요?\"\n"
                + "}\n"
                + "},\n"
                + "{\n"
                + "\"key\": \"4\",\n"
                + "\"subject\": \"일관성과 연습\",\n"
                + "\"questionRs\": {\n"
                + "\"questionKey\": \"4\",\n"
                + "\"question\": \"이 습관을 얼마나 일관되게 수행할 수 있었는지 및 진행 상황을 어떻게 모니터링하고 계십니까?\"\n"
                + "}\n"
                + "},\n"
                + "{\n"
                + "\"key\": \"5\",\n"
                + "\"subject\": \"유지 및 생활 방식 통합\",\n"
                + "\"questionRs\": {\n"
                + "\"questionKey\": \"5\",\n"
                + "\"question\": \"이 습관을 오랜 시간 유지하는 데 문제가 있었나요? 이를 어떻게 해결해 오셨나요?\"\n"
                + "}\n"
                + "}\n"
                + "]";
    }

    private List<HabitPreQuestionRs> createExpected() {
        HabitPreQuestionRs rs1 = createHabitPreQuestionRs("1", "일반적 인식과 의도", "1",
                "현재 무엇을 개발하려고 노력하고 있으며, 해당 습관이 왜 중요한지 생각하십니까?");
        HabitPreQuestionRs rs2 = createHabitPreQuestionRs("2", "목표 명확성과 계획", "2",
                "이 습관과 관련된 구체적인 목표를 설정했습니까? 이러한 목표를 달성하기 위해 어떤 계획을 세우셨나요?");
        HabitPreQuestionRs rs3 = createHabitPreQuestionRs("3", "초기 조치 및 소규모 단계", "3",
                "이 습관을 시작하려면 어떤 가장 작은 단계를 취했거나 계획하셨나요?");
        HabitPreQuestionRs rs4 = createHabitPreQuestionRs("4", "일관성과 연습", "4",
                "이 습관을 얼마나 일관되게 수행할 수 있었는지 및 진행 상황을 어떻게 모니터링하고 계십니까?");
        HabitPreQuestionRs rs5 = createHabitPreQuestionRs("5", "유지 및 생활 방식 통합", "5",
                "이 습관을 오랜 시간 유지하는 데 문제가 있었나요? 이를 어떻게 해결해 오셨나요?");

        return new ArrayList<>(List.of(rs1, rs2, rs3, rs4, rs5));
    }

    private HabitPreQuestionRs createHabitPreQuestionRs(String subjectKey, String subject, String questionKey,
                                                        String question) {
        return new HabitPreQuestionRs(subjectKey, subject,
                new QuestionRs(questionKey, question));
    }
}
