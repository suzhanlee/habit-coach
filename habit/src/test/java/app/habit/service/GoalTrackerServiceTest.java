package app.habit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.any;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.habit.domain.GoalTracker;
import app.habit.domain.Habit;
import app.habit.repository.GoalTrackerRepository;
import java.util.Optional;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class GoalTrackerServiceTest {

    @Mock
    private GoalTrackerRepository goalTrackerRepository;

    @InjectMocks
    private GoalTrackerService goalTrackerService;

    private Long habitId;
    private Habit habit;

    @BeforeEach
    void setUp() {
        habitId = 1L;
        habit = new Habit(habitId);
    }

    @Test
    @DisplayName("사용자의 습관의 목표 측정기를 가져온다.")
    void find_goal_tracker_by_habit() {
        // given
        Long expectedGoalTrackerId = 1L;

        // when
        when(goalTrackerRepository.findByHabitId(habitId)).thenReturn(Optional.of(expectedGoalTrackerId));

        Long result = goalTrackerService.findGoalTrackerIdOrCreate(habit);

        // then
        assertThat(result).isEqualTo(expectedGoalTrackerId);

        verify(goalTrackerRepository, times(1)).findByHabitId(habitId);
    }

    @Test
    @DisplayName("사용자의 습관에 목표 측정기가 없다면 생성해서 반환한다.")
    void create_goal_tracker_if_not_present() {
        // given
        Long expectedGoalTrackerId = 1L;

        when(goalTrackerRepository.findByHabitId(habitId)).thenReturn(Optional.empty());
        when(goalTrackerRepository.save(any(GoalTracker.class))).thenAnswer(invocation -> {
            GoalTracker argument = invocation.getArgument(0);
            ReflectionTestUtils.setField(argument, "id", expectedGoalTrackerId);
            return argument;
        });

        // when
        Long result = goalTrackerService.findGoalTrackerIdOrCreate(habit);

        // then
        assertThat(result).isEqualTo(expectedGoalTrackerId);
        verify(goalTrackerRepository, times(1)).findByHabitId(habitId);
        verify(goalTrackerRepository, times(1)).save(any(GoalTracker.class));
    }
}
