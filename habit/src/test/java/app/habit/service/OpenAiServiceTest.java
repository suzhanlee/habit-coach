package app.habit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.habit.domain.Habit;
import app.habit.domain.factory.PreQuestionPromptFactory;
import app.habit.dto.openaidto.HabitPreQuestionRs;
import app.habit.dto.openaidto.QuestionRs;
import app.habit.repository.HabitRepository;
import app.habit.service.gpt.coach.PreQuestionGptCoach;
import app.habit.service.gpt.request.RequestPrompt;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class OpenAiServiceTest {

    @MockBean
    private HabitRepository habitRepository;

    @MockBean
    private PreQuestionGptCoach preQuestionGptCoach;

    @MockBean
    private PreQuestionPromptFactory preQuestionPromptFactory;

    @MockBean
    private HabitFormingPhaseService habitFormingPhaseService;

    @MockBean
    private HabitAssessmentManagerService habitAssessmentManagerService;

    @MockBean
    private SubjectService subjectService;

    @MockBean
    private QuestionService questionService;

    @Autowired
    private OpenAiService openAiService;

    @Test
    @DisplayName("습관 형성 모델의 단계 판단을 위한 사전 설문지를 가져온다.")
    public void get_habit_pre_questions() throws Exception {
        // given
        Long givenHabitId = 1L;
        RequestPrompt givenPrompt = new RequestPrompt();
        Long expectedHabitFormingPhaseId = 1L;
        Long expectedHabitAssessmentManagerId = 1L;
        Long expectedSubjectId = 1L;
        Long expectedQuestionId = 1L;

        String testUrl = "https://api.openai.com/v1/chat/completions";

        List<HabitPreQuestionRs> expectedHabitPreQuestionRsList = createHabitPreQuestionRs();

        // when
        when(habitRepository.findById(eq(givenHabitId))).thenReturn(Optional.of(new Habit(givenHabitId)));
        when(preQuestionPromptFactory.create()).thenReturn(givenPrompt);
        when(preQuestionGptCoach.requestPreQuestions(givenPrompt, testUrl))
                .thenReturn(CompletableFuture.completedFuture(expectedHabitPreQuestionRsList));
        when(habitFormingPhaseService.findHabitFormingPhaseIdOrCreate(givenHabitId)).thenReturn(
                expectedHabitFormingPhaseId);
        when(habitAssessmentManagerService.save(expectedHabitFormingPhaseId)).thenReturn(
                expectedHabitAssessmentManagerId);
        when(subjectService.save(anyString(), anyString(), eq(expectedHabitAssessmentManagerId))).thenReturn(
                expectedSubjectId);
        when(questionService.save(anyString(), anyString(), eq(expectedSubjectId))).thenReturn(expectedQuestionId);

        CompletableFuture<List<HabitPreQuestionRs>> future = openAiService.getHabitPreQuestions(givenHabitId);
        List<HabitPreQuestionRs> result = future.get();

        // then
        assertThat(result).isEqualTo(expectedHabitPreQuestionRsList);

        verify(habitRepository, times(1)).findById(givenHabitId);
        verify(preQuestionPromptFactory, times(1)).create();
        verify(preQuestionGptCoach, times(1)).requestPreQuestions(givenPrompt, testUrl);
        verify(habitFormingPhaseService, times(1)).findHabitFormingPhaseIdOrCreate(expectedHabitFormingPhaseId);
        verify(habitAssessmentManagerService, times(1)).save(expectedHabitAssessmentManagerId);
        verify(subjectService, times(3)).save(anyString(), anyString(), eq(expectedHabitAssessmentManagerId));
        verify(questionService, times(3)).save(anyString(), anyString(), eq(expectedSubjectId));
    }

    private List<HabitPreQuestionRs> createHabitPreQuestionRs() {
        HabitPreQuestionRs expectedHabitPreQuestionRs1 = new HabitPreQuestionRs("key1", "subject1",
                new QuestionRs("questionKey1", "question1"));
        HabitPreQuestionRs expectedHabitPreQuestionRs2 = new HabitPreQuestionRs("key2", "subject2",
                new QuestionRs("questionKey2", "question2"));
        HabitPreQuestionRs expectedHabitPreQuestionRs3 = new HabitPreQuestionRs("key3", "subject3",
                new QuestionRs("questionKey3", "question3"));
        return List.of(expectedHabitPreQuestionRs1,
                expectedHabitPreQuestionRs2, expectedHabitPreQuestionRs3);
    }
}
