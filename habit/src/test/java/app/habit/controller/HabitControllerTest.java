package app.habit.controller;

import static app.habit.controller.Path.PRE_QUESTIONS;
import static org.assertj.core.api.Assertions.assertThat;

import app.habit.dto.HabitPreQuestionRs;
import app.habit.dto.QuestionRs;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;

@DisplayName("gpt api 관련 기능")
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
class HabitControllerTest {

    @Test
    @DisplayName("습관 형성 모델 평가를 위한 사전 질문을 가져온다.")
    void get_pre_questions_for_habit_formation_model_evaluation() {
        // given
        long pathVariable = 1;

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().pathParam("phaseId", pathVariable)
                .when().get(PRE_QUESTIONS)
                .then()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(createActualRs(response)).usingRecursiveComparison().isEqualTo(createExpectedRs());
    }

    private List<HabitPreQuestionRs> createActualRs(ExtractableResponse<Response> response) {
        return response.as(new TypeRef<List<HabitPreQuestionRs>>() {
        });
    }

    private List<HabitPreQuestionRs> createExpectedRs() {
        HabitPreQuestionRs rs1 = createHabitPreQuestionRs("1", "일반적인 인식 및 의도", "1",
                "현재 어떤 습관을 기르기 위해 노력하고 있으며, 그것이 왜 중요하다고 생각하시나요?");
        HabitPreQuestionRs rs2 = createHabitPreQuestionRs("2", "목표 명확성 및 계획", "2",
                "이 습관과 관련된 구체적인 목표를 설정하셨나요? 이러한 목표를 달성하기 위해 어떤 단계를 계획했나요?");
        HabitPreQuestionRs rs3 = createHabitPreQuestionRs("3", "초기 조치 및 작은 단계", "3",
                "이 습관을 시작하기 위해 취했거나 취할 계획인 가장 작은 조치는 무엇입니까?");
        HabitPreQuestionRs rs4 = createHabitPreQuestionRs("4", "일관성과 실천", "4",
                "이 습관을 얼마나 일관되게 수행할 수 있었으며 진행 상황을 어떻게 모니터링합니까?");
        HabitPreQuestionRs rs5 = createHabitPreQuestionRs("5", "유지관리 및 라이프스타일 통합", "5",
                "시간이 지나도 이 습관을 유지하는 데 어려움을 겪은 적이 있습니까? 당신은 그들을 어떻게 처리했는가?");

        return new ArrayList<>(List.of(rs1, rs2, rs3, rs4, rs5));
    }

    private HabitPreQuestionRs createHabitPreQuestionRs(String subjectKey, String subject, String questionKey,
                                                        String question) {
        return new HabitPreQuestionRs(subjectKey, subject,
                new QuestionRs(questionKey, question));
    }
}
