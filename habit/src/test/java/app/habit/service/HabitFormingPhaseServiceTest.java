package app.habit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.habit.domain.HabitFormingPhase;
import app.habit.repository.HabitFormingPhaseRepository;
import app.habit.service.HabitFormingPhaseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HabitFormingPhaseServiceTest {

    @Mock
    private HabitFormingPhaseRepository habitFormingPhaseRepository;

    @InjectMocks
    private HabitFormingPhaseService habitFormingPhaseService;

    @Test
    void save_habit_forming_phase() {
        // given
        Long habitId = 1L;

        // when
        ArgumentCaptor<HabitFormingPhase> phaseArgumentCaptor = ArgumentCaptor.forClass(HabitFormingPhase.class);
        when(habitFormingPhaseRepository.save(phaseArgumentCaptor.capture())).thenAnswer(invocation -> {
            HabitFormingPhase phase = invocation.getArgument(0);
            phase.setId(12);
            return phase;
        });

        Long result = habitFormingPhaseService.createHabitFormingPhase(habitId);

        // then
        assertThat(result).isEqualTo(12);
        verify(habitFormingPhaseRepository, times(1)).save(any(HabitFormingPhase.class));
        assertThat(phaseArgumentCaptor.getValue().getHabitId()).isEqualTo(habitId);
    }
}
