package app.habit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.habit.domain.GoalTracker;
import app.habit.domain.Habit;
import app.habit.domain.Track;
import app.habit.domain.TrackType;
import app.habit.domain.User;
import app.habit.dto.habitdto.CreateHabitRq;
import app.habit.dto.habitdto.CreateHabitRs;
import app.habit.dto.habitdto.CreateHabitTrackRq;
import app.habit.dto.habitdto.CreateHabitTrackRs;
import app.habit.dto.habitdto.SpecificHabitTrackListRq;
import app.habit.dto.habitdto.SpecificHabitTrackListRs;
import app.habit.dto.habitdto.UserHabitListRq;
import app.habit.dto.habitdto.UserHabitListRs;
import app.habit.repository.GoalTrackerRepository;
import app.habit.repository.HabitRepository;
import app.habit.repository.TrackRepository;
import app.habit.repository.UserRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HabitServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HabitRepository habitRepository;

    @Mock
    private GoalTrackerRepository goalTrackerRepository;

    @Mock
    private TrackRepository trackRepository;

    @InjectMocks
    private HabitService habitService;

    @Test
    @DisplayName("사용자는 습관을 생성할 수 있다.")
    void create_habit() {
        // given
        long givenUserId = 1L;
        User givenUser = new User(givenUserId);
        String habitName = "exercise";
        CreateHabitRq createHabitRq = new CreateHabitRq(givenUserId, habitName);

        long givenHabitId = 1;

        // when
        when(userRepository.findById(givenUserId)).thenReturn(Optional.of(givenUser));

        ArgumentCaptor<Habit> habitCaptor = ArgumentCaptor.forClass(Habit.class);
        when(habitRepository.save(habitCaptor.capture())).thenAnswer(invocation -> {
            Habit habit = invocation.getArgument(0);
            habit.setId(givenHabitId);
            habit.addUserId(givenUser.getId());
            return habit;
        });

        CreateHabitRs result = habitService.createHabit(createHabitRq);

        // then
        verify(userRepository).findById(1L);
        verify(habitRepository).save(habitCaptor.capture());

        assertThat(result.getHabitId()).isEqualTo(givenHabitId);
    }

    @Test
    @DisplayName("사용자의 습관 목록을 조회한다.")
    void get_user_habit_list() {
        // given
        long givenUserId = 1L;
        User givenUser = new User(givenUserId);

        UserHabitListRq rq = new UserHabitListRq(1);

        List<UserHabitListRs> rs = new ArrayList<>();
        rs.add(new UserHabitListRs(1, "read a book"));
        rs.add(new UserHabitListRs(2, "exercise"));

        // when
        when(userRepository.findById(1L)).thenReturn(Optional.of(givenUser));
        when(habitRepository.findUserHabitListByUserId(givenUserId)).thenReturn(rs);

        List<UserHabitListRs> result = habitService.getHabitList(rq);

        // then
        verify(userRepository).findById(1L);
        verify(habitRepository).findUserHabitListByUserId(givenUserId);

        assertThat(result).isEqualTo(rs);
    }

    @Test
    @DisplayName("사용자가 원하는 습관을 삭제한다.")
    void delete_habit() {
        // when
        habitService.deleteHabit(1);

        // then
        verify(habitRepository).deleteById(1L);
    }

    @Test
    @DisplayName("사용자는 자신을 습관을 추적할 수 있다.")
    void track_habit() {
        // given
        long givenHabitId = 1;
        Habit givenHabit = new Habit(givenHabitId);

        long givenGoalTrackerId = 1;
        long givenTrackId = 1;

        CreateHabitTrackRq rq = new CreateHabitTrackRq(givenHabitId, LocalDate.of(2024, 3, 3),
                "checked");

        // when
        when(habitRepository.findById(givenHabitId)).thenReturn(Optional.of(givenHabit));

        ArgumentCaptor<GoalTracker> goalTrackerArgumentCaptor = ArgumentCaptor.forClass(GoalTracker.class);
        when(goalTrackerRepository.save(goalTrackerArgumentCaptor.capture())).thenAnswer(invocation -> {
            GoalTracker goalTracker = invocation.getArgument(0);
            goalTracker.setId(givenGoalTrackerId);
            goalTracker.addHabitId(givenHabitId);
            return goalTracker;
        });

        ArgumentCaptor<Track> trackArgumentCaptor = ArgumentCaptor.forClass(Track.class);
        when(trackRepository.save(trackArgumentCaptor.capture())).thenAnswer(invocation -> {
            Track track = invocation.getArgument(0);
            track.setId(givenTrackId);
            track.addGoalTrackerId(givenGoalTrackerId);
            return track;
        });

        CreateHabitTrackRs result = habitService.trackHabit(rq);

        // then
        verify(habitRepository).findById(givenHabitId);
        verify(goalTrackerRepository).save(goalTrackerArgumentCaptor.capture());
        verify(trackRepository).save(trackArgumentCaptor.capture());

        GoalTracker goalTracker = goalTrackerArgumentCaptor.getValue();
        assertThat(goalTracker.getHabitId()).isEqualTo(givenHabitId);

        Track track = trackArgumentCaptor.getValue();
        assertThat(track.getGoalTrackerId()).isEqualTo(givenGoalTrackerId);

        assertThat(result).isEqualTo(new CreateHabitTrackRs(givenGoalTrackerId, givenTrackId));
    }

    @Test
    @DisplayName("사용자는 원하는 달의 자신의 특정 습관 진행 상황을 조회할 수 있다.")
    void find_user_habit_track_list_by_specific_month() {
        // given
        long goalTrackerId = 1;
        int year = 2024;
        int month = 3;
        SpecificHabitTrackListRq rq = new SpecificHabitTrackListRq(goalTrackerId, year, month);
        List<SpecificHabitTrackListRs> rs = List.of(
                new SpecificHabitTrackListRs(goalTrackerId, LocalDate.of(year, month, 3), TrackType.CHECKED),
                new SpecificHabitTrackListRs(goalTrackerId, LocalDate.of(year, month, 4), TrackType.UNCHECKED),
                new SpecificHabitTrackListRs(goalTrackerId, LocalDate.of(year, month, 5), TrackType.CHECKED)
        );

        // when
        when(trackRepository.findTracksByGoalTrackerIdAndYearAndMonth(goalTrackerId, year, month))
                .thenReturn(rs);

        List<SpecificHabitTrackListRs> result = habitService.getSpecificHabitTrackList(rq);

        // then
        verify(trackRepository).findTracksByGoalTrackerIdAndYearAndMonth(goalTrackerId, year, month);
        assertThat(result).isEqualTo(rs);
    }
}
