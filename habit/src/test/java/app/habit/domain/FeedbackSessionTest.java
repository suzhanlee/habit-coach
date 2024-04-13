//package app.habit.domain;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//class FeedbackSessionTest {
//
//    @Test
//    @DisplayName("단일 피드백 세션에 해당하는 프롬프트 조각을 생성한다.")
//    void create_prompt() {
//        // given
//        FeedbackSession feedbackSession = new FeedbackSession("1", "질문1", "대답1");
//
//        // when
//        String result = feedbackSession.createPrompt();
//
//        // then
//        assertThat(result).isEqualTo("""
//                number : 1
//                question : 질문1
//                answer : 대답1
//                """);
//    }
//}
