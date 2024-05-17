package app.habit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.habit.dto.executionintentiondto.UserExecutionIntentionRs;
import app.habit.repository.ExecutionIntentionRepository;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ExecutionIntentionServiceTest {

    @Mock
    private ExecutionIntentionRepository executionIntentionRepository;

    @InjectMocks
    private ExecutionIntentionService executionIntentionService;

    @Test
    @DisplayName("사용자가 습관에 설정한 실행의도를 가져온다.")
    void get_execution_intention() {
        // given
        long executionIntentionId = 1L;
        UserExecutionIntentionRs expectedRs = new UserExecutionIntentionRs();
        when(executionIntentionRepository.findUserSmartGoalRsById(executionIntentionId)).thenReturn(
                Optional.of(expectedRs));

        // when
        UserExecutionIntentionRs result = executionIntentionService.getExecutionIntention(executionIntentionId);

        // then
        assertThat(result).isEqualTo(expectedRs);
        verify(executionIntentionRepository, times(1)).findUserSmartGoalRsById(executionIntentionId);
    }

    @Test
    @DisplayName("실행의도와 smart 목표가 같은 goalTracker에 속하지 않는다면 예외를 던진다.")
    void get_execution_intention_exception() {
        // given
        long executionIntentionId = 1L;
        when(executionIntentionRepository.findUserSmartGoalRsById(executionIntentionId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(NoSuchElementException.class, () -> {
            executionIntentionService.getExecutionIntention(executionIntentionId);
        });
        verify(executionIntentionRepository, times(1)).findUserSmartGoalRsById(executionIntentionId);
    }

    @Test
    @DisplayName("사용자가 습관에 설정한 실행의도를 삭제한다.")
    void delete_execution_intention() {
        // given
        long executionIntentionId = 1L;

        // when
        executionIntentionService.deleteExecutionIntention(executionIntentionId);

        // then
        verify(executionIntentionRepository, times(1)).deleteById(executionIntentionId);
    }
}
