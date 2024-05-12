package app.habit.service;

import app.habit.domain.HabitAssessmentManager;
import app.habit.repository.HabitAssessmentManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class HabitAssessmentManagerService {

    private final HabitAssessmentManagerRepository habitAssessmentManagerRepository;

    public Long save(Long habitFormingPhaseId) {
        HabitAssessmentManager habitAssessmentManager = new HabitAssessmentManager();
        habitAssessmentManagerRepository.save(habitAssessmentManager);
        habitAssessmentManager.addHabitFormingPhaseId(habitFormingPhaseId);
        return habitAssessmentManager.getId();
    }

    public void savePhaseInfo(String phaseType, String phaseDescription, Long habitAssessmentManagerId) {
        HabitAssessmentManager habitAssessmentManager = habitAssessmentManagerRepository.findById(
                habitAssessmentManagerId).orElseThrow();
        habitAssessmentManager.assess(phaseType, phaseDescription);
    }
}
