package app.habit.service;

import app.habit.domain.FeedbackSession;
import app.habit.repository.FeedbackSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FeedbackSessionService {

    private final FeedbackSessionRepository feedbackSessionRepository;

    public void save(String sessionKey, String question, Long feedbackModuleId) {
        FeedbackSession feedbackSession = new FeedbackSession(sessionKey, question);
        feedbackSessionRepository.save(feedbackSession);
        feedbackSession.addFeedbackModuleId(feedbackModuleId);
    }
}
