package app.habit.controller;

import app.habit.dto.habitdto.CreateHabitRq;
import app.habit.dto.habitdto.CreateHabitRs;
import app.habit.dto.habitdto.CreateHabitTrackRq;
import app.habit.dto.habitdto.CreateHabitTrackRs;
import app.habit.dto.habitdto.SpecificHabitTrackListRq;
import app.habit.dto.habitdto.SpecificHabitTrackListRs;
import app.habit.dto.habitdto.UserHabitListRq;
import app.habit.dto.habitdto.UserHabitListRs;
import app.habit.dto.habitdto.UserHabitPhaseFeedbackRq;
import app.habit.dto.habitdto.UserHabitPhaseFeedbackRs;
import app.habit.dto.openaidto.SpecificUserHabitInfoRs;
import app.habit.service.HabitService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HabitController {

    private final HabitService habitService;

    @PostMapping("/habit")
    public CreateHabitRs createHabit(@RequestBody CreateHabitRq rq) {
        return habitService.createHabit(rq);
    }

    @GetMapping("/habit")
    public List<UserHabitListRs> getUserHabitList(@RequestBody UserHabitListRq rq) {
        return habitService.getHabitList(rq);
    }

    @DeleteMapping("/habit/{habitId}")
    public void deleteHabit(@PathVariable("habitId") long habitId) {
        habitService.deleteHabit(habitId);
    }

    @PostMapping("/habit/track")
    public CreateHabitTrackRs createHabitTrack(@RequestBody CreateHabitTrackRq rq) {
        return habitService.trackHabit(rq);
    }

    @GetMapping("/habit/track")
    public List<SpecificHabitTrackListRs> getSpecificHabitTrackList(@RequestBody SpecificHabitTrackListRq rq) {
        return habitService.getSpecificHabitTrackList(rq);
    }

    @GetMapping("/habit/{habitId}")
    public SpecificUserHabitInfoRs getSpecificUserHabitInfo(@PathVariable("habitId") long habitId) {
        return habitService.getSpecificUserHabitInfo(habitId);
    }

    @GetMapping("/habit/feedback/{habitFormingPhaseId}")
    public UserHabitPhaseFeedbackRs getUserHabitPhaseFeedback(@PathVariable("habitFormingPhaseId") Long habitFormingPhaseId) {
        return habitService.getUserHabitPhaseFeedback(habitFormingPhaseId);
    }
}
