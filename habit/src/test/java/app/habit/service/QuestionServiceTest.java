package app.habit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.habit.domain.Question;
import app.habit.repository.QuestionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QuestionService questionService;

    @Test
    @DisplayName("특정 주제에 해당하는 사전 질문을 저장한다.")
    void save() {
        // given
        String questionKey = "key";
        String questionContent = "content";
        Long expectedQuestionId = 1L;
        Long subjectId = 1L;

        // when
        when(questionRepository.save(any(Question.class))).thenAnswer(invocation -> {
            Question argument = invocation.getArgument(0);
            ReflectionTestUtils.setField(argument, "id", expectedQuestionId);
            return argument;
        });

        Long result = questionService.save(questionKey, questionContent, subjectId);

        // then
        assertThat(result).isEqualTo(expectedQuestionId);
        verify(questionRepository).save(argThat(question ->
                question.getQuestionKey().equals(questionKey) && question.getQuestion().equals(questionContent)
                        && question.getSubjectId().equals(subjectId)));
    }
}
