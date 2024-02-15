package app.habit.controller;

import static app.habit.controller.Path.PHASE;
import static app.habit.controller.Path.PRE_QUESTIONS;
import static app.habit.domain.HabitFormingPhaseType.CONSIDERATION_STAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import app.habit.dto.HabitPreQuestionRs;
import app.habit.dto.PhaseEvaluationAnswerRq;
import app.habit.dto.PhaseEvaluationRq;
import app.habit.dto.PhaseEvaluationRs;
import app.habit.dto.PhaseQuestionRs;
import app.habit.dto.QuestionRs;
import app.habit.dto.UserHabitPreQuestionRq;
import app.habit.dto.UserHabitPreQuestionRs;
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

    @Test
    @DisplayName("사용자의 습관 수준을 습관 형성 모델에 따라 평가한다.")
    void evaluate_user_habit_phase() {
        // given
        PhaseEvaluationRq givenRq = createPhaseEvaluationRq(1, createPhaseEvaluationAnswers());

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().body(givenRq).contentType(APPLICATION_JSON_VALUE)
                .when().post(PHASE)
                .then()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(createActualPhaseEvaluationRs(response)).usingRecursiveComparison()
                .isEqualTo(createExpectedPhaseEvaluationRs());
    }

    private PhaseEvaluationRq createPhaseEvaluationRq(long phaseId,
                                                      List<PhaseEvaluationAnswerRq> phaseEvaluationAnswers) {
        return new PhaseEvaluationRq(phaseId, phaseEvaluationAnswers);
    }

    private List<PhaseEvaluationAnswerRq> createPhaseEvaluationAnswers() {
        PhaseEvaluationAnswerRq rq1 = new PhaseEvaluationAnswerRq("1",
                "저는 매일 밤 잠자리에 들기 전에 책을 읽는 습관을 기르는 데 집중하고 있습니다. 독서는 긴장을 풀고 기술로부터의 연결을 끊는 데 도움이 되어 수면의 질을 향상시키기 때문에 중요하다고 믿습니다.");
        PhaseEvaluationAnswerRq rq2 = new PhaseEvaluationAnswerRq("2",
                "네, 매일 밤 잠들기 전 30분 이상 책을 읽는 것을 목표로 삼았습니다. 이를 달성하기 위해 관심 있는 책 목록을 선택하고 독서등과 책을 한 무더기씩 놓아두었습니다. 방해가 되지 않도록 예정된 독서 시간 한 시간 전에 모든 전자 장치를 끄기로 결정했습니다.");
        PhaseEvaluationAnswerRq rq3 = new PhaseEvaluationAnswerRq("3",
                "내가 취한 가장 작은 조치는 가능한 한 쉽게 끝까지 읽을 수 있도록 하루에 한 페이지부터 시작하는 것입니다. 이것이 내 일상의 일부가 되면서 점차적으로 읽는 양을 늘리는 것이 내 계획입니다.");
        PhaseEvaluationAnswerRq rq4 = new PhaseEvaluationAnswerRq("4",
                "지금까지 일주일에 며칠 밤만 책을 읽었습니다. 매일 밤 읽은 페이지 수를 일지에 기록하여 진행 상황을 추적하고 있습니다.");
        PhaseEvaluationAnswerRq rq5 = new PhaseEvaluationAnswerRq("5",
                "아직 시작하는 단계라 습관을 유지하는 것이 어려울 정도는 아닙니다. 스케줄이 바빠지면 시간을 내기가 어려울 것으로 예상하지만, 일정을 잡아서 해결하려고 합니다. 내 달력에서는 독서 시간을 더 엄격하게 정해요.");

        return new ArrayList<>(List.of(rq1, rq2, rq3, rq4, rq5));
    }

    private PhaseEvaluationRs createActualPhaseEvaluationRs(ExtractableResponse<Response> response) {
        return response.as(new TypeRef<PhaseEvaluationRs>() {
        });
    }

    private PhaseEvaluationRs createExpectedPhaseEvaluationRs() {
        return new PhaseEvaluationRs(1, CONSIDERATION_STAGE,
                "목표 설정 및 계획의 고려 단계에서 개인은 단순히 변화를 만들거나 새로운 습관을 개발하는 것에 대해 생각하는 것에서 이를 수행하는 방법을 적극적으로 계획하는 것으로 전환하고 있습니다. 이 단계에는 모호한 아이디어나 욕구를 구체적이고 실행 가능한 목표로 구체화하는 작업이 포함됩니다. 이는 '변화를 만들고 싶다'에서 '정확히 내가 할 일과 방법은 다음과 같습니다.'로 이동하는 것입니다. 초점은 구체성, 타당성 및 앞으로의 여정에 대한 계획에 있습니다.");
    }

    @Test
    @DisplayName("사용자의 습관 형성 단계에 따른 사전 질문을 가져온다.")
    void get_pre_questions_according_to_user_habit_phase() {
        // given
        UserHabitPreQuestionRq givenRq = createUserHabitPreQuestionRq();

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().body(givenRq).contentType(APPLICATION_JSON_VALUE)
                .when().post(Path.PHASE_QUESTION)
                .then()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(createActualUserHabitPreQuestionRs(response)).usingRecursiveComparison().isEqualTo(createExpectedUserHabitPreQuestionRs());
    }

    private UserHabitPreQuestionRq createUserHabitPreQuestionRq() {
        return new UserHabitPreQuestionRq(1, CONSIDERATION_STAGE);
    }

    private List<UserHabitPreQuestionRs> createActualUserHabitPreQuestionRs(ExtractableResponse<Response> response) {
        return response.as(new TypeRef<List<UserHabitPreQuestionRs>>() {});
    }

    private List<UserHabitPreQuestionRs> createExpectedUserHabitPreQuestionRs() {
        UserHabitPreQuestionRs rs1 = new UserHabitPreQuestionRs(1, "1", "자원과 지원에 대한 질문들",
                createPhaseFirstModuleQuestion());
        UserHabitPreQuestionRs rs2 = new UserHabitPreQuestionRs(2, "2", "자원과 지원에 대한 질문들",
                createPhaseSecondModuleQuestion());
        UserHabitPreQuestionRs rs3 = new UserHabitPreQuestionRs(3, "3", "초기 성공 및 동기부여에 대한 질문들",
                createPhaseThirdModuleQuestion());

        return new ArrayList<>(List.of(rs1, rs2, rs3));
    }

    private List<PhaseQuestionRs> createPhaseFirstModuleQuestion() {
        PhaseQuestionRs rs1 = new PhaseQuestionRs("1", "이 작은 단계들을 실천하기 위해 필요한 자원은 무엇인가요?");
        PhaseQuestionRs rs2 = new PhaseQuestionRs("2", "작은 단계들을 수행하는 데 있어서 가장 큰 동기는 무엇인가요?");
        PhaseQuestionRs rs3 = new PhaseQuestionRs("3", "이 작은 단계들을 어떻게 일상의 다른 활동이나 습관과 연결할 수 있을까요?");

        return new ArrayList<>(List.of(rs1, rs2, rs3));
    }

    private List<PhaseQuestionRs> createPhaseSecondModuleQuestion() {
        PhaseQuestionRs rs1 = new PhaseQuestionRs("1", "이 작은 단계들을 실천하기 위해 필요한 자원은 무엇인가요?");
        PhaseQuestionRs rs2 = new PhaseQuestionRs("2", "이 목표를 달성하는 데 도움이 될 수 있는 외부의 지원이나 자원은 무엇인가요?");
        PhaseQuestionRs rs3 = new PhaseQuestionRs("3", "이 작은 단계들을 지속적으로 유지하기 위해 필요한 것은 무엇이라고 생각하나요?");

        return new ArrayList<>(List.of(rs1, rs2, rs3));
    }

    private List<PhaseQuestionRs> createPhaseThirdModuleQuestion() {
        PhaseQuestionRs rs1 = new PhaseQuestionRs("1", "이 작은 단계들을 성공적으로 수행했을 때 자신에게 어떻게 보상하거나 칭찬할 계획인가요?");
        PhaseQuestionRs rs2 = new PhaseQuestionRs("2", "작은 단계들을 수행하는 데 있어서 가장 큰 동기는 무엇인가요?");
        PhaseQuestionRs rs3 = new PhaseQuestionRs("3", "이 작은 단계들을 어떻게 일상의 다른 활동이나 습관과 연결할 수 있을까요?");

        return new ArrayList<>(List.of(rs1, rs2, rs3));
    }
}
