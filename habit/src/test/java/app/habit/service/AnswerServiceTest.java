package app.habit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.habit.domain.Answer;
import app.habit.domain.Subject;
import app.habit.repository.AnswerRepository;
import app.habit.repository.SubjectRepository;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class AnswerServiceTest {

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private AnswerService answerService;

    @Test
    public void save() {
        // given
        String answerKey = "key";
        String answerContent = "content";
        Long habitAssessmentManagerId = 1L;
        Long expectedAnswerId = 1L;

        when(answerRepository.save(any(Answer.class))).thenAnswer(invocation -> {
            Answer argument = invocation.getArgument(0);
            ReflectionTestUtils.setField(argument, "id", expectedAnswerId);
            return argument;
        });
        when(subjectRepository.findByKey(habitAssessmentManagerId, answerKey)).thenReturn(Optional.of(new Subject()));

        // when
        Long result = answerService.save(answerKey, answerContent, habitAssessmentManagerId);

        // then
        assertThat(result).isEqualTo(expectedAnswerId);
        verify(answerRepository).save(argThat(answer ->
                answer.getAnswerKey().equals(answerKey) && answer.getAnswer().equals(answerContent)
        ));
        verify(subjectRepository).findByKey(habitAssessmentManagerId, answerKey);
    }

    @Test
    public void save_throws_exception_when_subject_not_found() {
        // given
        String answerKey = "key";
        String answerContent = "content";
        Long habitAssessmentManagerId = 1L;

        // when
        when(answerRepository.save(any(Answer.class))).thenReturn(new Answer(answerKey, answerContent));
        when(subjectRepository.findByKey(habitAssessmentManagerId, answerKey)).thenReturn(Optional.empty());

        // then
        assertThrows(NoSuchElementException.class, () -> {
            answerService.save(answerKey, answerContent, habitAssessmentManagerId);
        });
    }
}
