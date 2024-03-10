package app.habit.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class FeedbackModuleTest {

    @ParameterizedTest
    @DisplayName("키 값에 맞는 피드백 세션을 찾아 세션에 응답을 저장한다.")
    @MethodSource("sessionAnswerData")
    void add_answer_to_session(String givenKey, String answer, String expected) {
        // given
        FeedbackModule givenFeedbackModule = createGivenFeedbackModule();

        // when
        givenFeedbackModule.addAnswer(givenKey, answer);

        // then
        for (FeedbackSession feedbackSession : givenFeedbackModule.getFeedbackSessions()) {
            if (feedbackSession.getSessionKey().equals(givenKey)) {
                assertThat(feedbackSession.getAnswer()).isEqualTo(expected);
            }
        }
    }

    private FeedbackModule createGivenFeedbackModule() {
        return new FeedbackModule(
                "일상에 통합하는 작은 단계 설정",
                new ArrayList<>(
                        List.of(
                                new FeedbackSession("1", "질문1"),
                                new FeedbackSession("2", "질문2"),
                                new FeedbackSession("3", "질문3"))
                ));
    }

    private static Stream<Arguments> sessionAnswerData() {
        return Stream.of(
                arguments("1", "대답1", "대답1"),
                arguments("2", "대답2", "대답2"),
                arguments("3", "대답3", "대답3")
        );
    }

    @Test
    @DisplayName("피드백 모듈을 사용하여 gpt에게 보낼 프롬프트 조각을 생성한다.")
    void create_prompt() {
        // given
        FeedbackModule givenFeedbackModule = createGivenFeedbackModule();
        givenFeedbackModule.addAnswer("1", "대답1");
        givenFeedbackModule.addAnswer("2", "대답2");
        givenFeedbackModule.addAnswer("3", "대답3");

        // when
        String result = givenFeedbackModule.createPrompt();

        // then
        assertThat(result).isEqualTo("""
                subject : 일상에 통합하는 작은 단계 설정
                number : 1
                question : 질문1
                answer : 대답1
                number : 2
                question : 질문2
                answer : 대답2
                number : 3
                question : 질문3
                answer : 대답3
                """);
    }
}
