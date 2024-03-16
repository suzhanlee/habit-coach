package app.habit.service.gpt;

import app.habit.domain.HabitFormingPhase;
import app.habit.repository.HabitFormingPhaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class HabitFormingPhaseService {

    private final HabitFormingPhaseRepository habitFormingPhaseRepository;

    public Long save(Long habitId) {
        HabitFormingPhase habitFormingPhase = new HabitFormingPhase();
        habitFormingPhaseRepository.save(habitFormingPhase);
        habitFormingPhase.addHabitId(habitId);
        return habitFormingPhase.getId();
    }
}
