//package app.habit.domain;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//class SubjectTest {
//
//    @Test
//    @DisplayName("subject으로 필요한 프롬프트 조각을 만든다.")
//    void create_prompt_in_subject_section() {
//        // given
//        Subject subject = new Subject("1", "General awareness and intention", new Question("1", "질문"),
//                new Answer("1", "대답"));
//
//        // when
//        String result = subject.createPrompt();
//
//        // then
//        assertThat(result).isEqualTo("""
//                ## 1. General awareness and intention
//                Question : 질문
//                Answer : 대답
//                """);
//    }
//}
