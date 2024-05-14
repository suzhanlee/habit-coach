package app.habit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.habit.domain.HabitAssessmentManager;
import app.habit.repository.HabitAssessmentManagerRepository;
import app.habit.service.HabitAssessmentManagerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HabitAssessmentManagerServiceTest {

    @Mock
    private HabitAssessmentManagerRepository habitAssessmentManagerRepository;

    @InjectMocks
    private HabitAssessmentManagerService habitAssessmentManagerService;

    @Test
    @DisplayName("습관 평가 매니저를 저장하고, 외래키 습관형성단계 아이디를 저장한다.")
    void save_habit_assessment_manager() {
        // given
        Long habitFormingPhaseId = 1L;

        // when
        ArgumentCaptor<HabitAssessmentManager> argumentCaptor = ArgumentCaptor.forClass(
                HabitAssessmentManager.class);
        when(habitAssessmentManagerRepository.save(argumentCaptor.capture())).thenAnswer(invocation -> {
            HabitAssessmentManager habitAssessmentManager = invocation.getArgument(0);
            habitAssessmentManager.setId(12L);
            return habitAssessmentManager;
        });

        Long result = habitAssessmentManagerService.save(habitFormingPhaseId);

        // then
        assertThat(result).isEqualTo(12);
        verify(habitAssessmentManagerRepository, times(1)).save(any(HabitAssessmentManager.class));
        assertThat(argumentCaptor.getValue().getHabitFormingPhaseId()).isEqualTo(habitFormingPhaseId);
    }
}
