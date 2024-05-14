package app.habit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.habit.domain.FeedbackModule;
import app.habit.repository.FeedbackModuleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class FeedbackModuleServiceTest {

    @Mock
    private FeedbackModuleRepository feedbackModuleRepository;

    @InjectMocks
    private FeedbackModuleService feedbackModuleService;

    @Test
    @DisplayName("사용자의 습관 형성 단계에 맞는 피드백 모듈을 저장한다.")
    void save_feedback_module_according_to_habit_forming_phase() {
        // given
        String key = "key";
        String subject = "subject";
        Long habitFormingPhaseId = 1L;
        Long expectedFeedbackModuleId = 1L;

        // when
        when(feedbackModuleRepository.save(any(FeedbackModule.class))).thenAnswer(invocation -> {
            FeedbackModule argument = invocation.getArgument(0);
            ReflectionTestUtils.setField(argument, "id", expectedFeedbackModuleId);
            return argument;
        });

        Long result = feedbackModuleService.save(key, subject, habitFormingPhaseId);

        // then
        assertThat(result).isEqualTo(expectedFeedbackModuleId);
        verify(feedbackModuleRepository).save(argThat(feedbackModule ->
                feedbackModule.getKey().equals(key) && feedbackModule.getSubject().equals(subject)
                        && feedbackModule.getHabitFormingPhaseId().equals(expectedFeedbackModuleId)));
    }
}
