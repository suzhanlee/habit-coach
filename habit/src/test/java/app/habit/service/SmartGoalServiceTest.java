package app.habit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.habit.domain.GoalTracker;
import app.habit.domain.Habit;
import app.habit.domain.HabitFormingPhaseType;
import app.habit.domain.Smart;
import app.habit.domain.factory.SmartGoalFeedbackPromptFactory;
import app.habit.dto.smartdto.CreateSmartGoalRq;
import app.habit.dto.smartdto.CreateSmartGoalRs;
import app.habit.dto.smartdto.UpdateSmartGoalRq;
import app.habit.dto.smartdto.UpdateSmartGoalRs;
import app.habit.dto.smartdto.UserSmartGoalRs;
import app.habit.repository.GoalTrackerRepository;
import app.habit.repository.HabitAssessmentManagerRepository;
import app.habit.repository.HabitFormingPhaseRepository;
import app.habit.repository.HabitRepository;
import app.habit.repository.SmartRepository;
import app.habit.service.gpt.coach.SmartGoalFeedbackGptCoach;
import app.habit.service.gpt.request.RequestPrompt;
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
public class SmartGoalServiceTest {

    @MockBean
    private GoalTrackerRepository goalTrackerRepository;

    @MockBean
    private HabitAssessmentManagerRepository habitAssessmentManagerRepository;

    @MockBean
    private HabitFormingPhaseRepository habitFormingPhaseRepository;

    @MockBean
    private HabitRepository habitRepository;

    @MockBean
    private SmartGoalFeedbackPromptFactory feedbackPromptFactory;

    @MockBean
    private SmartGoalFeedbackGptCoach feedbackGptCoach;

    @MockBean
    private SmartRepository smartRepository;

    @Autowired
    private SmartGoalService smartGoalService;

    private String testUrl;

    @BeforeEach
    void setUp() {
        testUrl = "https://api.openai.com/v1/chat/completions";
    }

    @Test
    @DisplayName("smart 목표를 생성한다.")
    void create_smart_goal() throws Exception {
        // given
        Long givenGoalTrackerId = 1L;
        Long habitId = 1L;
        Long habitFormingPhaseId = 1L;
        HabitFormingPhaseType habitFormingPhaseType = HabitFormingPhaseType.CONSIDERATION_STAGE;

        String smartGoal = "smart goal";
        String feedback = "Good goal!";
        RequestPrompt requestPrompt = new RequestPrompt();

        CreateSmartGoalRq rq = new CreateSmartGoalRq(givenGoalTrackerId, smartGoal);

        Long expectedSmartId = 1L;
        CreateSmartGoalRs expectedCreateSmartGoalRs = new CreateSmartGoalRs(expectedSmartId, feedback);

        // when
        when(goalTrackerRepository.findById(givenGoalTrackerId)).thenReturn(Optional.of(
                new GoalTracker(givenGoalTrackerId, habitId)));
        when(habitFormingPhaseRepository.findIdByHabitId(habitId)).thenReturn(Optional.of(habitFormingPhaseId));
        when(habitAssessmentManagerRepository.findPhaseTypeByHabitFormingPhaseId(habitFormingPhaseId))
                .thenReturn(Optional.of(habitFormingPhaseType));
        when(habitRepository.findById(habitId)).thenReturn(Optional.of(new Habit(habitId, "exercise")));
        when(feedbackPromptFactory.create(anyString(), eq(habitFormingPhaseType), eq(smartGoal))).thenReturn(
                requestPrompt);
        when(feedbackGptCoach.requestSmartGoalFeedback(any(), any())).thenReturn(
                CompletableFuture.completedFuture(feedback));
        when(smartRepository.save(any(Smart.class))).thenAnswer(invocation -> {
            Smart argument = invocation.getArgument(0);
            ReflectionTestUtils.setField(argument, "id", expectedSmartId);
            return argument;
        });

        CreateSmartGoalRs result = smartGoalService.createSmartGoal(rq).get();

        // then
        assertThat(result).isEqualTo(expectedCreateSmartGoalRs);

        verify(goalTrackerRepository, times(1)).findById(givenGoalTrackerId);
        verify(habitAssessmentManagerRepository, times(1)).findPhaseTypeByHabitFormingPhaseId(habitFormingPhaseId);
        verify(habitRepository, times(1)).findById(habitId);
        verify(feedbackPromptFactory, times(1)).create(anyString(), eq(habitFormingPhaseType), eq(smartGoal));
        verify(feedbackGptCoach, times(1)).requestSmartGoalFeedback(requestPrompt, testUrl);
        verify(smartRepository, times(1)).save(
                argThat(smart -> smart.getGoal().equals(smartGoal) &&
                        smart.getFeedback().equals(feedback) &&
                        smart.getGoalTrackerId().equals(givenGoalTrackerId)));
    }

    @Test
    @DisplayName("smart 목표를 조회한다.")
    void get_smart_goal() {
        // given
        Long givenSmartGoalId = 1L;
        UserSmartGoalRs expectedUserSmartGoalRs = new UserSmartGoalRs("exercise", "smart goal feedback");
        when(smartRepository.findUserSmartGoalRsById(givenSmartGoalId)).thenReturn(expectedUserSmartGoalRs);

        // when
        UserSmartGoalRs result = smartGoalService.getSmartGoal(givenSmartGoalId);

        // then
        assertThat(result).isEqualTo(expectedUserSmartGoalRs);
    }

    @Test
    @DisplayName("smart 목표를 삭제한다.")
    void delete_smart_goal() {
        // given
        Long givenSmartGoalId = 1L;

        // when
        smartGoalService.deleteSmartGoal(givenSmartGoalId);

        // then
        verify(smartRepository).deleteById(givenSmartGoalId);
    }

    @Test
    @DisplayName("smart 목표를 수정한다.")
    void update_smart_goal() throws Exception {
        // given
        Long givenSmartGoalId = 1L;
        String smartGoal = "smart goal";
        Long goalTrackerId = 1L;
        Long habitId = 1L;
        String habitName = "exercise";
        Long habitFormingPhaseId = 1L;
        RequestPrompt requestPrompt = new RequestPrompt();
        HabitFormingPhaseType habitFormingPhaseType = HabitFormingPhaseType.CONSIDERATION_STAGE;

        UpdateSmartGoalRq rq = new UpdateSmartGoalRq(givenSmartGoalId, smartGoal);

        String expectedFeedback = "Updated feedback!";
        UpdateSmartGoalRs expectedRs = new UpdateSmartGoalRs(expectedFeedback);

        // when
        when(smartRepository.findById(givenSmartGoalId)).thenReturn(Optional.of(new Smart(givenSmartGoalId, goalTrackerId)));
        when(goalTrackerRepository.findById(goalTrackerId)).thenReturn(
                Optional.of(new GoalTracker(goalTrackerId, habitId)));
        when(habitRepository.findById(habitId)).thenReturn(Optional.of(new Habit(habitId, habitName)));
        when(habitFormingPhaseRepository.findIdByHabitId(habitId)).thenReturn(Optional.of(habitFormingPhaseId));
        when(habitAssessmentManagerRepository.findPhaseTypeByHabitFormingPhaseId(habitFormingPhaseId))
                .thenReturn(Optional.of(habitFormingPhaseType));
        when(feedbackPromptFactory.create(anyString(), eq(habitFormingPhaseType), eq(smartGoal))).thenReturn(
                requestPrompt);
        when(feedbackGptCoach.requestSmartGoalFeedback(requestPrompt, testUrl)).thenReturn(
                CompletableFuture.completedFuture(expectedFeedback));

        UpdateSmartGoalRs result = smartGoalService.updateSmartGoal(rq).get();

        // then
        assertThat(result).isEqualTo(expectedRs);

        verify(smartRepository, times(1)).findById(givenSmartGoalId);
        verify(goalTrackerRepository, times(1)).findById(goalTrackerId);
        verify(habitRepository, times(1)).findById(habitId);
        verify(habitFormingPhaseRepository, times(1)).findIdByHabitId(habitId);
        verify(habitAssessmentManagerRepository, times(1)).findPhaseTypeByHabitFormingPhaseId(habitFormingPhaseId);
        verify(feedbackPromptFactory, times(1)).create(anyString(), eq(habitFormingPhaseType), eq(smartGoal));
        verify(feedbackGptCoach, times(1)).requestSmartGoalFeedback(requestPrompt, testUrl);
    }
}
