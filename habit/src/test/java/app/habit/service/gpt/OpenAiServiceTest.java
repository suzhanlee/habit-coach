package app.habit.service.gpt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.habit.domain.HabitAssessmentManager;
import app.habit.domain.HabitAssessmentManagerFactory;
import app.habit.domain.HabitFormingPhase;
import app.habit.domain.HabitFormingPhaseType;
import app.habit.domain.PreQuestionCoach;
import app.habit.domain.PromptFactory;
import app.habit.dto.HabitPreQuestionRs;
import app.habit.repository.HabitAssessmentManagerRepository;
import app.habit.repository.HabitFormingPhaseRepository;
import app.habit.service.gpt.request.RequestPrompt;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
class OpenAiServiceTest {

    @Mock
    private PreQuestionCoach preQuestionCoach;

    @Mock
    private PromptFactory promptFactory;

    @Mock
    private HabitAssessmentManagerFactory habitAssessmentManagerFactory;

    @Mock
    private HabitFormingPhaseRepository habitFormingPhaseRepository;

    @InjectMocks
    private OpenAiService openAiService;

    @Test
    @DisplayName("사전 질문을 요청하고 저장한다.")
    void request_pre_questions_and_save() {
        // given
        Long givenId = 1L;
        String givenType = "사전 질문";
        String userPrompt = "";
        String givenUrl = "https://api.openai.com/v1/chat/completions";

        RequestPrompt requestPrompt = new RequestPrompt();
        List<HabitPreQuestionRs> adviceContent = List.of(new HabitPreQuestionRs());
        HabitAssessmentManager habitAssessmentManager = new HabitAssessmentManager();
        HabitFormingPhase habitFormingPhase = new HabitFormingPhase(givenId);

        // when
        when(promptFactory.createRequestBody(givenType, userPrompt)).thenReturn(requestPrompt);
        when(preQuestionCoach.advice(requestPrompt, givenUrl)).thenReturn(adviceContent);
        when(habitAssessmentManagerFactory.createHabitAssessmentManager(adviceContent)).thenReturn(habitAssessmentManager);
        when(habitFormingPhaseRepository.findById(eq(givenId))).thenReturn(Optional.of(habitFormingPhase));

        List<HabitPreQuestionRs> result = openAiService.getHabitPreQuestions(givenId, givenType, userPrompt);

        // then
        verify(promptFactory).createRequestBody(givenType, userPrompt);
        verify(preQuestionCoach).advice(requestPrompt, givenUrl);
        verify(habitAssessmentManagerFactory).createHabitAssessmentManager(adviceContent);
        verify(habitFormingPhaseRepository).findById(givenId);

        assertThat(result).isEqualTo(adviceContent);
    }
}
