package app.habit.service;

import app.habit.domain.HabitFormingPhase;
import app.habit.repository.HabitFormingPhaseRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class HabitFormingPhaseService {

    private final HabitFormingPhaseRepository habitFormingPhaseRepository;

    public Long findHabitFormingPhaseIdOrCreate(Long habitId) {
        return habitFormingPhaseRepository.findIdByHabitId(habitId)
                .orElseGet(() -> createHabitFormingPhase(habitId));
    }

    public Long createHabitFormingPhase(Long habitId) {
        HabitFormingPhase habitFormingPhase = new HabitFormingPhase();
        habitFormingPhaseRepository.save(habitFormingPhase);
        habitFormingPhase.addHabitId(habitId);
        return habitFormingPhase.getId();
    }
}
