package app.habit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.habit.domain.Subject;
import app.habit.dto.openaidto.SingleEvaluationPromptDto;
import app.habit.repository.SubjectRepository;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class SubjectServiceTest {

    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private SubjectService subjectService;

    @Test
    @DisplayName("습관 평가에 맞는 주제를 저장한다.")
    void save() {
        // given
        String subjectKey = "key";
        String subjectContent = "content";
        Long habitAssessmentManagerId = 1L;
        Long expectedSubjectId = 1L;

        // when
        when(subjectRepository.save(any(Subject.class))).thenAnswer(invocation -> {
            Subject argument = invocation.getArgument(0);
            ReflectionTestUtils.setField(argument, "id", expectedSubjectId);
            return argument;
        });

        Long result = subjectService.save(subjectKey, subjectContent, habitAssessmentManagerId);

        // then
        assertThat(result).isEqualTo(expectedSubjectId);
        verify(subjectRepository).save(argThat(subject ->
                subject.getSubjectKey().equals(subjectKey) && subject.getSubject().equals(subjectContent)
                        && subject.getHabitAssessmentManagerId().equals(habitAssessmentManagerId)));
    }

    @Test
    @DisplayName("습관 평가를 위한 정보들을 가져온다.")
    void get_infos_for_total_evaluation_prompt() {
        // given
        Long habitAssessmentManagerId = 1L;
        String subjectKey1 = "subjectKey1";
        String subjectKey2 = "subjectKey2";
        List<String> subjectKeys = Arrays.asList(subjectKey1, subjectKey2);
        SingleEvaluationPromptDto dto1 = new SingleEvaluationPromptDto(subjectKey1, "subject1", "question1", "answer1");
        SingleEvaluationPromptDto dto2 = new SingleEvaluationPromptDto(subjectKey2, "subject2", "question2", "answer2");

        when(subjectRepository.findSubjectIdsByAssessmentManagerId(habitAssessmentManagerId))
                .thenReturn(subjectKeys);
        when(subjectRepository.findPromptDtoBySubjectKey(subjectKey1)).thenReturn(dto1);
        when(subjectRepository.findPromptDtoBySubjectKey(subjectKey2)).thenReturn(dto2);

        // when
        List<SingleEvaluationPromptDto> result = subjectService.getTotalEvaluationPromptDto(habitAssessmentManagerId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsAll(Arrays.asList(dto1, dto2));
        verify(subjectRepository).findSubjectIdsByAssessmentManagerId(habitAssessmentManagerId);
        verify(subjectRepository).findPromptDtoBySubjectKey(subjectKey1);
        verify(subjectRepository).findPromptDtoBySubjectKey(subjectKey2);
    }
}
