package app.habit.service;

import app.habit.dto.executionintentiondto.UserExecutionIntentionRs;
import app.habit.repository.ExecutionIntentionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ExecutionIntentionService {

    private final ExecutionIntentionRepository executionIntentionRepository;

    public UserExecutionIntentionRs getExecutionIntention(long executionIntentionId) {
        return executionIntentionRepository.findUserSmartGoalRsById(executionIntentionId).orElseThrow();
    }

    public void deleteExecutionIntention(long executionIntentionId) {
        executionIntentionRepository.deleteById(executionIntentionId);
    }
}
