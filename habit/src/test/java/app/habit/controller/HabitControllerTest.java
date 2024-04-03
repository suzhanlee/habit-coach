package app.habit.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import app.habit.dto.habitdto.CreateHabitRq;
import app.habit.dto.habitdto.CreateHabitTrackRq;
import app.habit.dto.habitdto.SpecificHabitTrackListRq;
import app.habit.dto.habitdto.UserHabitListRq;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;

@DisplayName("habit api 관련 기능")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class HabitControllerTest {

    @Test
    @DisplayName("사용자가 만들고 싶은 습관을 생성한다.")
    void create_habit() {
        // given
        CreateHabitRq givenRq = new CreateHabitRq(1L, "exercise");

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().body(givenRq).contentType(APPLICATION_JSON_VALUE)
                .when().post("/habit")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        JsonPath actual = response.jsonPath();
        assertThat(actual.getLong("habitId")).isNotNull();
    }

    @Test
    @DisplayName("사용자의 습관 목록을 가져온다.")
    void get_user_habit_list() {
        // given
        UserHabitListRq givenRq = new UserHabitListRq(1);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().body(givenRq).contentType(APPLICATION_JSON_VALUE)
                .when().get("/habit")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Map<String, Object>> userHabitList = response.jsonPath().getList("$");
        assertThat(userHabitList).isNotEmpty();
        assertUserHabitList(userHabitList);
    }

    private void assertUserHabitList(List<Map<String, Object>> userHabitList) {
        userHabitList.forEach(this::assertUserHabit);
    }

    private void assertUserHabit(Map<String, Object> userHabit) {
        assertThat(userHabit).containsKeys("habitId", "habitName");
        assertThat(userHabit.get("habitId")).isNotNull();
        assertThat(userHabit.get("habitName")).isNotNull();
    }

    @Test
    @DisplayName("사용자의 습관을 삭제한다.")
    void delete_habit() {
        // given
        long pathVariable = 7;

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().pathParam("habitId", pathVariable).log().all()
                .when().delete("/habit/{habitId}")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("사용자 습관을 진행상황을 표시한다.")
    void track_user_habit() {
        // given
        CreateHabitTrackRq givenRq = new CreateHabitTrackRq(8, LocalDate.of(2024, 3, 10), "checked");

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().body(givenRq).contentType(APPLICATION_JSON_VALUE)
                .when().post("/habit/track")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        JsonPath actual = response.jsonPath();
        assertThat(actual.getLong("goalTrackerId")).isNotNull();
        assertThat(actual.getLong("trackId")).isNotNull();
    }

    @Test
    @DisplayName("사용자의 특정 습관의 진행상황을 가져온다.")
    void get_specific_habit_track_list() {
        // given
        SpecificHabitTrackListRq givenRq = new SpecificHabitTrackListRq(12, 2024, 3);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().body(givenRq).contentType(APPLICATION_JSON_VALUE)
                .when().get("/habit/track")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Map<String, Object>> actual = response.jsonPath().getList("$");
        assertThat(actual).isNotEmpty();
        assertThatSpecificHabitTrackStructure(actual);
    }

    private void assertThatSpecificHabitTrackStructure(List<Map<String, Object>> actual) {
        for (Map<String, Object> specificHabitTrackInfo : actual) {
            assertThat(specificHabitTrackInfo).containsKeys("trackId", "trackDateTime", "trackType");
            assertThat(specificHabitTrackInfo.get("trackId")).isNotNull();
            assertThat(specificHabitTrackInfo.get("trackType")).isNotNull();
            assertThat(specificHabitTrackInfo.get("trackDateTime")).isNotNull();
        }
    }

    @Test
    @DisplayName("사용자의 특정 습관을 상세조회해 가져온다.")
    void get_specific_user_habit_info() {
        // given
        long pathVariable = 1;

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().pathParam("habitId", pathVariable)
                .when().get("/habit/{habitId}")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        JsonPath actual = response.jsonPath();
        assertThat(actual.getString("habitName")).isNotNull();
        assertThatUserHabitFormingPhaseDto(actual);
        assertThatUserGoalTrackerDto(actual);
    }

    private void assertThatUserHabitFormingPhaseDto(JsonPath actual) {
        Map<String, Object> userHabitFormingPhaseDto = (Map<String, Object>) actual.get("userHabitFormingPhaseDto");
        assertThat(userHabitFormingPhaseDto).containsKeys("habitFormingPhaseId", "habitAssessmentManagerId", "habitFormingPhaseType");
        assertThat(userHabitFormingPhaseDto.get("habitFormingPhaseId")).isNotNull();
        assertThat(userHabitFormingPhaseDto.get("habitAssessmentManagerId")).isNotNull();
        assertThat(userHabitFormingPhaseDto.get("habitFormingPhaseType")).isNotNull();
    }

    private void assertThatUserGoalTrackerDto(JsonPath actual) {
        Map<String, Object> userGoalTrackerDto = (Map<String, Object>) actual.get("userGoalTrackerDto");
        assertThat(userGoalTrackerDto).containsKeys("goalTrackerId", "smartId", "executionIntentionId");
        assertThat(userGoalTrackerDto.get("goalTrackerId")).isNotNull();
        assertThat(userGoalTrackerDto.get("smartId")).isNotNull();
        assertThat(userGoalTrackerDto.get("executionIntentionId")).isNotNull();
    }

    @Test
    @DisplayName("사용자의 특정 습관의 습관 형성 단계 피드백을 가져온다.")
    void get_user_habit_phase_feedback() {
        // given
        long pathVariable = 4;

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().pathParam("habitFormingPhaseId", pathVariable)
                .when().get("/habit/feedback/{habitFormingPhaseId}")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        JsonPath actual = response.jsonPath();
        assertThat(actual.getString("habitFormingPhaseType")).isNotNull();
        assertThat(actual.getString("description")).isNotNull();
        assertThat(Optional.ofNullable(actual.get("userHabitFeedbackModules"))).isNotNull();

        List<Map<String, Object>> userHabitFeedbackModules = actual.get("userHabitFeedbackModules");
        assertThatUserHabitFeddbackModules(userHabitFeedbackModules);
    }

    private void assertThatUserHabitFeddbackModules(List<Map<String, Object>> userHabitFeedbackModules) {
        for (Map<String, Object> userHabitFeedbackModule : userHabitFeedbackModules) {
            assertThat(userHabitFeedbackModule).containsKeys("userHabitFeedbackDto", "feedbackSessionDtos");
            assertThat(userHabitFeedbackModule.get("userHabitFeedbackDto")).isNotNull();
            assertThat(userHabitFeedbackModule.get("feedbackSessionDtos")).isNotNull();

            assertThatUserHabitFeedbackDto(userHabitFeedbackModule);
            assertThatFeedbackSessionDtos(userHabitFeedbackModule);
        }
    }

    private void assertThatUserHabitFeedbackDto(Map<String, Object> userHabitFeedbackModule) {
        Map<String, Object> userHabitFeedbackDto = (Map<String, Object>) userHabitFeedbackModule.get(
                "userHabitFeedbackDto");
        assertThat(userHabitFeedbackDto.get("feedbackModuleId")).isNotNull();
        assertThat(userHabitFeedbackDto.get("subject")).isNotNull();
        assertThat(userHabitFeedbackDto.get("feedback")).isNotNull();
    }

    private void assertThatFeedbackSessionDtos(Map<String, Object> userHabitFeedbackModule) {
        List<Map<String, Object>> feedbackSessionDtos = (List<Map<String, Object>>) userHabitFeedbackModule.get(
                "feedbackSessionDtos");
        for (Map<String, Object> feedbackSessionDto : feedbackSessionDtos) {
            assertThat(feedbackSessionDto).containsKeys("question", "answer");
            assertThat(feedbackSessionDto.get("question")).isNotNull();
            assertThat(feedbackSessionDto.get("answer")).isNotNull();
        }
    }
}
