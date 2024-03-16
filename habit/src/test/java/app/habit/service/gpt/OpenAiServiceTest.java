package app.habit.service.gpt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.habit.domain.Habit;
import app.habit.domain.factory.PromptFactory;
import app.habit.dto.openaidto.HabitPreQuestionRs;
import app.habit.repository.HabitAssessmentManagerRepository;
import app.habit.repository.HabitRepository;
import app.habit.service.gpt.coach.PreQuestionGptCoach;
import app.habit.service.gpt.request.RequestPrompt;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OpenAiServiceTest {

    @Mock
    private HabitRepository habitRepository;

    @Mock
    private PreQuestionGptCoach preQuestionGptCoach;

    @Mock
    private PromptFactory promptFactory;

    @Mock
    private HabitFormingPhaseService habitFormingPhaseService;

    @Mock
    private HabitAssessmentManagerService habitAssessmentManagerService;

    @Mock
    private SubjectService subjectService;

    @Mock
    private QuestionService questionService;

    @Mock
    private HabitAssessmentManagerRepository habitAssessmentManagerRepository;

    @InjectMocks
    private OpenAiService openAiService;

    @Test
    @DisplayName("사전질문을 요청하고 저장한다.")
    void request_habit_pre_questions_and_save() {
        // given
        Long habitId = 1L;
        String type = "사전질문";
        String prompt = "prompt";

        long subjectId = 0;
        long questionId = 0;

        String url = "https://api.openai.com/v1/chat/completions";

        RequestPrompt requestPrompt = new RequestPrompt();
        List<HabitPreQuestionRs> preQuestionRs = new ArrayList<>();

        when(promptFactory.create(type, prompt)).thenReturn(requestPrompt);
        when(preQuestionGptCoach.requestPreQuestions(requestPrompt, url)).thenReturn(preQuestionRs);
        when(habitRepository.findById(habitId)).thenReturn(Optional.of(new Habit()));
        when(habitFormingPhaseService.save(anyLong())).thenReturn(1L);
        when(habitAssessmentManagerService.save(anyLong())).thenReturn(1L);
        when(subjectService.save(anyString(), anyString(), anyLong())).thenReturn(subjectId += 1);
        when(questionService.save(anyString(), anyString(), anyLong())).thenReturn(questionId += 1);

        // when
        List<HabitPreQuestionRs> result = openAiService.getHabitPreQuestions(habitId, type, prompt);

        // then
        assertEquals(preQuestionRs, result);
        verify(promptFactory, times(1)).create(type, prompt);
        verify(preQuestionGptCoach, times(1)).requestPreQuestions(requestPrompt, url);
        verify(habitRepository, times(1)).findById(habitId);
        verify(habitFormingPhaseService, times(1)).save(anyLong());
        verify(habitAssessmentManagerService, times(1)).save(anyLong());
        verify(subjectService, times(preQuestionRs.size())).save(anyString(), anyString(), anyLong());
        verify(questionService, times(preQuestionRs.size())).save(anyString(), anyString(), anyLong());
    }
}
