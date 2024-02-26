package app.habit.controller;

import static app.habit.controller.Path.PHASE;
import static app.habit.controller.Path.PRE_QUESTIONS;
import static app.habit.domain.HabitFormingPhaseType.CONSIDERATION_STAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import app.habit.dto.PhaseAnswerRq;
import app.habit.dto.PhaseEvaluationAnswerRq;
import app.habit.dto.PhaseEvaluationRq;
import app.habit.dto.PhaseEvaluationRs;
import app.habit.dto.PhaseFeedbackRq;
import app.habit.dto.PhaseFeedbackRs;
import app.habit.dto.PhaseQuestionRs;
import app.habit.dto.UserHabitPreQuestionRq;
import app.habit.dto.UserHabitPreQuestionRs;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        List<Map<String, Object>> questions = response.jsonPath().getList("$");
        assertThat(questions).isNotEmpty();
        assertQuestions(questions);
    }

    private void assertQuestions(List<Map<String, Object>> questions) {
        questions.forEach(this::assertPreQuestionStructure);
    }

    private void assertPreQuestionStructure(Map<String, Object> question) {
        assertThat(question).containsKeys("key", "subject", "questionRs");
        assertThat(question.get("key")).isNotNull();
        assertThat(question.get("subject")).isNotNull();
        assertQuestionRs(question);
    }

    private void assertQuestionRs(Map<String, Object> question) {
        Map<String, String> questionRs = (Map<String, String>) question.get("questionRs");
        assertThat(questionRs).containsKeys("questionKey", "question");
        assertThat(questionRs.get("questionKey")).isNotNull();
        assertThat(questionRs.get("question")).isNotNull();
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

        JsonPath actual = response.jsonPath();
        assertThat(actual.getLong("habitAssessmentManagerId")).isNotNull();
        assertThat(actual.getString("phaseType")).isNotNull();
        assertThat(actual.getString("phaseDescription")).isNotNull();
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
        assertThat(createActualUserHabitPreQuestionRs(response)).usingRecursiveComparison()
                .isEqualTo(createExpectedUserHabitPreQuestionRs());
    }

    private UserHabitPreQuestionRq createUserHabitPreQuestionRq() {
        return new UserHabitPreQuestionRq(1, CONSIDERATION_STAGE);
    }

    private List<UserHabitPreQuestionRs> createActualUserHabitPreQuestionRs(ExtractableResponse<Response> response) {
        return response.as(new TypeRef<>() {
        });
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

    @Test
    @DisplayName("사용자가 답변을 제출하면 피드백이 온다.")
    void get_feedback_according_to_answer() {
        // given
        PhaseFeedbackRq givenRq = createPhaseFeedbackRq(1, createPhaseAnswers());

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().body(givenRq).contentType(APPLICATION_JSON_VALUE)
                .when().post()
                .then()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(createActualPhaseFeedbackRs(response)).usingRecursiveComparison()
                .isEqualTo(createExpectedPhaseFeedbackRs());
    }

    private PhaseFeedbackRq createPhaseFeedbackRq(long feedbackModuleId, List<PhaseAnswerRq> phaseAnswers) {
        return new PhaseFeedbackRq(feedbackModuleId, phaseAnswers);
    }

    private List<PhaseAnswerRq> createPhaseAnswers() {
        PhaseAnswerRq rq1 = new PhaseAnswerRq("1", "가장 먼저, 매일 아침 10분간 스트레칭을 시작할 계획입니다.");
        PhaseAnswerRq rq2 = new PhaseAnswerRq("2", "이를 위해, 매일 아침 알람을 설정하고, 침대 옆에 스트레칭 매트를 놓을 것입니다.");
        PhaseAnswerRq rq3 = new PhaseAnswerRq("3", "이 작은 습관을 통해 하루를 더 활기차게 시작하고, 유연성을 향상시키고 싶습니다.");

        return new ArrayList<>(List.of(rq1, rq2, rq3));
    }

    private PhaseFeedbackRs createActualPhaseFeedbackRs(ExtractableResponse<Response> response) {
        return response.as(new TypeRef<>() {
        });
    }

    private PhaseFeedbackRs createExpectedPhaseFeedbackRs() {
        return new PhaseFeedbackRs("일상에 통합하는 작은 단계 설정",
                "매일 아침 스트레칭은 좋은 시작입니다. 이를 일상에 통합하기 위해, 저녁에는 다음 날의 준비를 해두세요. 이러한 준비는 스트레칭을 더욱 쉽게 만들 것입니다.");
    }
}
