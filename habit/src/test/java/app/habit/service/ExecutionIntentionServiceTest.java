package app.habit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.habit.domain.ExecutionIntention;
import app.habit.domain.GoalTracker;
import app.habit.domain.Habit;
import app.habit.domain.HabitFormingPhaseType;
import app.habit.domain.Smart;
import app.habit.domain.factory.ExecutionIntentionFeedbackPromptFactory;
import app.habit.dto.executionintentiondto.CreateExecutionIntentionRq;
import app.habit.dto.executionintentiondto.CreateExecutionIntentionRs;
import app.habit.dto.executionintentiondto.UpdateExecutionIntentionRq;
import app.habit.dto.executionintentiondto.UpdateExecutionIntentionRs;
import app.habit.dto.executionintentiondto.UserExecutionIntentionRs;
import app.habit.dto.smartdto.UpdateSmartGoalRq;
import app.habit.dto.smartdto.UpdateSmartGoalRs;
import app.habit.repository.ExecutionIntentionRepository;
import app.habit.repository.GoalTrackerRepository;
import app.habit.repository.HabitAssessmentManagerRepository;
import app.habit.repository.HabitFormingPhaseRepository;
import app.habit.repository.HabitRepository;
import app.habit.service.gpt.coach.ExecutionIntentionFeedbackGptCoach;
import app.habit.service.gpt.request.RequestPrompt;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
public class ExecutionIntentionServiceTest {

    @MockBean
    private GoalTrackerRepository goalTrackerRepository;

    @MockBean
    private HabitAssessmentManagerRepository habitAssessmentManagerRepository;

    @MockBean
    private HabitFormingPhaseRepository habitFormingPhaseRepository;

    @MockBean
    private HabitRepository habitRepository;

    @MockBean
    private ExecutionIntentionFeedbackPromptFactory feedbackPromptFactory;

    @MockBean
    private ExecutionIntentionFeedbackGptCoach feedbackGptCoach;

    @MockBean
    private ExecutionIntentionRepository executionIntentionRepository;

    @Autowired
    private ExecutionIntentionService executionIntentionService;

    private String testUrl;

    @BeforeEach
    void setUp() {
        testUrl = "https://api.openai.com/v1/chat/completions";
    }

    @Test
    @DisplayName("실행 의도를 생성한다.")
    void create_execution_intention() throws Exception {
        // given
        Long givenGoalTrackerId = 1L;
        Long habitId = 1L;
        Long habitFormingPhaseId = 1L;
        HabitFormingPhaseType habitFormingPhaseType = HabitFormingPhaseType.CONSIDERATION_STAGE;

        String executionIntention = "execution intention";
        String feedback = "Good execution intention!";
        RequestPrompt requestPrompt = new RequestPrompt();

        CreateExecutionIntentionRq rq = new CreateExecutionIntentionRq(givenGoalTrackerId, executionIntention);

        Long expectedExecutionIntentionId = 1L;
        CreateExecutionIntentionRs expectedRs = new CreateExecutionIntentionRs(expectedExecutionIntentionId, feedback);

        // when
        when(goalTrackerRepository.findById(givenGoalTrackerId)).thenReturn(Optional.of(
                new GoalTracker(givenGoalTrackerId, habitId)));
        when(habitFormingPhaseRepository.findIdByHabitId(habitId)).thenReturn(Optional.of(habitFormingPhaseId));
        when(habitAssessmentManagerRepository.findPhaseTypeByHabitFormingPhaseId(habitFormingPhaseId))
                .thenReturn(Optional.of(habitFormingPhaseType));
        when(habitRepository.findById(habitId)).thenReturn(Optional.of(new Habit(habitId, "exercise")));
        when(feedbackPromptFactory.create(anyString(), eq(habitFormingPhaseType), eq(executionIntention))).thenReturn(
                requestPrompt);
        when(feedbackGptCoach.requestExecutionIntentionFeedback(requestPrompt, testUrl)).thenReturn(
                CompletableFuture.completedFuture(feedback));
        when(executionIntentionRepository.save(any(ExecutionIntention.class))).thenAnswer(invocation -> {
            ExecutionIntention argument = invocation.getArgument(0);
            ReflectionTestUtils.setField(argument, "id", expectedExecutionIntentionId);
            return argument;
        });

        CreateExecutionIntentionRs result = executionIntentionService.createExecutionIntention(rq).get();

        // then
        assertThat(result).isEqualTo(expectedRs);

        verify(goalTrackerRepository, times(1)).findById(givenGoalTrackerId);
        verify(habitAssessmentManagerRepository, times(1)).findPhaseTypeByHabitFormingPhaseId(habitFormingPhaseId);
        verify(habitRepository, times(1)).findById(habitId);
        verify(feedbackPromptFactory, times(1)).create(anyString(), eq(habitFormingPhaseType), eq(executionIntention));
        verify(feedbackGptCoach, times(1)).requestExecutionIntentionFeedback(requestPrompt, testUrl);
        verify(executionIntentionRepository, times(1)).save(
                argThat(intention -> intention.getContent().equals(executionIntention) &&
                        intention.getFeedback().equals(feedback) &&
                        intention.getGoalTrackerId().equals(givenGoalTrackerId)));
    }

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

    @Test
    @DisplayName("실행 의도를 수정한다.")
    void update_execution_intention() throws Exception {
        // given
        Long givenExecutionIntentionId = 1L;
        String executionIntention = "execution intention";
        Long goalTrackerId = 1L;
        Long habitId = 1L;
        String habitName = "exercise";
        Long habitFormingPhaseId = 1L;
        RequestPrompt requestPrompt = new RequestPrompt();
        HabitFormingPhaseType habitFormingPhaseType = HabitFormingPhaseType.CONSIDERATION_STAGE;

        UpdateExecutionIntentionRq rq = new UpdateExecutionIntentionRq(givenExecutionIntentionId, executionIntention);

        String expectedFeedback = "Updated feedback!";
        UpdateExecutionIntentionRs expectedRs = new UpdateExecutionIntentionRs(expectedFeedback);

        // when
        when(executionIntentionRepository.findById(givenExecutionIntentionId)).thenReturn(
                Optional.of(new ExecutionIntention(givenExecutionIntentionId, goalTrackerId)));
        when(goalTrackerRepository.findById(goalTrackerId)).thenReturn(
                Optional.of(new GoalTracker(goalTrackerId, habitId)));
        when(habitRepository.findById(habitId)).thenReturn(Optional.of(new Habit(habitId, habitName)));
        when(habitFormingPhaseRepository.findIdByHabitId(habitId)).thenReturn(Optional.of(habitFormingPhaseId));
        when(habitAssessmentManagerRepository.findPhaseTypeByHabitFormingPhaseId(habitFormingPhaseId))
                .thenReturn(Optional.of(habitFormingPhaseType));
        when(feedbackPromptFactory.create(anyString(), eq(habitFormingPhaseType), eq(executionIntention))).thenReturn(
                requestPrompt);
        when(feedbackGptCoach.requestExecutionIntentionFeedback(requestPrompt, testUrl)).thenReturn(
                CompletableFuture.completedFuture(expectedFeedback));

        UpdateExecutionIntentionRs result = executionIntentionService.updateExecutionIntention(rq).get();

        // then
        assertThat(result).isEqualTo(expectedRs);

        verify(executionIntentionRepository, times(1)).findById(givenExecutionIntentionId);
        verify(goalTrackerRepository, times(1)).findById(goalTrackerId);
        verify(habitRepository, times(1)).findById(habitId);
        verify(habitFormingPhaseRepository, times(1)).findIdByHabitId(habitId);
        verify(habitAssessmentManagerRepository, times(1)).findPhaseTypeByHabitFormingPhaseId(habitFormingPhaseId);
        verify(feedbackPromptFactory, times(1)).create(anyString(), eq(habitFormingPhaseType), eq(executionIntention));
        verify(feedbackGptCoach, times(1)).requestExecutionIntentionFeedback(requestPrompt, testUrl);
    }
}
