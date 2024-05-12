package app.habit.service;

import app.habit.domain.FeedbackModule;
import app.habit.repository.FeedbackModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FeedbackModuleService {

    private final FeedbackModuleRepository feedbackModuleRepository;

    public Long save(String key, String subject, Long habitFormingPhaseId) {
        FeedbackModule feedbackModule = new FeedbackModule(key, subject);
        feedbackModuleRepository.save(feedbackModule);
        feedbackModule.addHabitFormingPhaseId(habitFormingPhaseId);
        return feedbackModule.getId();
    }
}
