package app.habit.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.habit.domain.FeedbackSession;
import app.habit.repository.FeedbackSessionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class FeedbackSessionServiceTest {

    @Mock
    private FeedbackSessionRepository feedbackSessionRepository;

    @InjectMocks
    private FeedbackSessionService feedbackSessionService;

    @Test
    @DisplayName("피드백 모듈에 맞는 피드백 세션을 저장한다.")
    void save_feedback_session_according_to_feedback_module() {
        // given
        String sessionKey = "sessionKey";
        String question = "question";
        Long feedbackModuleId = 1L;
        Long expectedFeedbackSessionId = 1L;

        // when
        when(feedbackSessionRepository.save(any(FeedbackSession.class))).thenAnswer(invocation -> {
            FeedbackSession argument = invocation.getArgument(0);
            ReflectionTestUtils.setField(argument, "id", expectedFeedbackSessionId);
            return argument;
        });

        feedbackSessionService.save(sessionKey, question, feedbackModuleId);

        // then
        verify(feedbackSessionRepository).save(argThat(feedbackSession ->
                feedbackSession.getSessionKey().equals(sessionKey) && feedbackSession.getQuestion().equals(question)
                        && feedbackSession.getFeedbackModuleId().equals(expectedFeedbackSessionId)));
    }
}
