package app.habit.service;

import app.habit.domain.GoalTracker;
import app.habit.domain.Habit;
import app.habit.domain.Track;
import app.habit.domain.User;
import app.habit.dto.habitdto.CreateHabitRq;
import app.habit.dto.habitdto.CreateHabitRs;
import app.habit.dto.habitdto.CreateHabitTrackRq;
import app.habit.dto.habitdto.CreateHabitTrackRs;
import app.habit.dto.habitdto.SpecificHabitTrackListRq;
import app.habit.dto.habitdto.SpecificHabitTrackListRs;
import app.habit.dto.habitdto.UserGoalTrackerDto;
import app.habit.dto.habitdto.UserHabitFormingPhaseDto;
import app.habit.dto.habitdto.UserHabitListRq;
import app.habit.dto.habitdto.UserHabitListRs;
import app.habit.dto.openaidto.SpecificUserHabitInfoRs;
import app.habit.repository.GoalTrackerRepository;
import app.habit.repository.HabitAssessmentManagerRepository;
import app.habit.repository.HabitFormingPhaseRepository;
import app.habit.repository.HabitRepository;
import app.habit.repository.TrackRepository;
import app.habit.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class HabitService {

    private final HabitRepository habitRepository;
    private final UserRepository userRepository;
    private final GoalTrackerRepository goalTrackerRepository;
    private final TrackRepository trackRepository;

    private final HabitFormingPhaseRepository habitFormingPhaseRepository;
    private final HabitAssessmentManagerRepository habitAssessmentManagerRepository;

    public CreateHabitRs createHabit(CreateHabitRq rq) {
        User user = userRepository.findById(rq.getUserId()).orElseThrow();
        Habit habit = new Habit(rq.getHabitName(), user.getId());
        habitRepository.save(habit);
        return new CreateHabitRs(habit);
    }

    public List<UserHabitListRs> getHabitList(UserHabitListRq rq) {
        User user = userRepository.findById(rq.getUserId()).orElseThrow();
        return habitRepository.findUserHabitListByUserId(user.getId());
    }

    public void deleteHabit(long habitId) {
        habitRepository.deleteById(habitId);
    }

    public CreateHabitTrackRs trackHabit(CreateHabitTrackRq rq) {
        Habit habit = habitRepository.findById(rq.getHabitId()).orElseThrow();

        GoalTracker goalTracker = new GoalTracker(habit.getId());
        goalTrackerRepository.save(goalTracker);

        Track track = new Track(rq.getTrackType(), rq.getTrackDateTime(), goalTracker.getId());
        trackRepository.save(track);

        return new CreateHabitTrackRs(goalTracker, track);
    }

    public List<SpecificHabitTrackListRs> getSpecificHabitTrackList(SpecificHabitTrackListRq rq) {
        return trackRepository.findTracksByGoalTrackerIdAndYearAndMonth(
                rq.getGoalTrackerId(),
                rq.getYear(),
                rq.getMonth()
        );
    }

    public SpecificUserHabitInfoRs getSpecificUserHabitInfo(long habitId) {
        Habit habit = habitRepository.findById(habitId).orElseThrow();

        UserHabitFormingPhaseDto phaseDto = habitAssessmentManagerRepository.findDtoByHabitFormingPhaseId(
                habitFormingPhaseRepository.findIdByHabitId(habit.getId()).orElseThrow()
        );

        UserGoalTrackerDto goalTrackerDto = goalTrackerRepository.findDtoById(
                habitRepository.findGoalTrackerIdById(habit.getId()).orElseThrow()
        );

        return new SpecificUserHabitInfoRs(habit.getName(), phaseDto, goalTrackerDto);
    }
}

