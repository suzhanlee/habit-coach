package app.habit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.habit.domain.FeedbackModule;
import app.habit.domain.FeedbackSession;
import app.habit.domain.Habit;
import app.habit.domain.HabitAssessmentManager;
import app.habit.domain.HabitFormingPhase;
import app.habit.domain.factory.EvaluationPromptFactory;
import app.habit.domain.factory.FeedbackPromptFactory;
import app.habit.domain.factory.PreQuestionPromptFactory;
import app.habit.domain.factory.SpecificPhasePreQuestionPromptFactory;
import app.habit.dto.openaidto.FeedbackPromptDto;
import app.habit.dto.openaidto.FeedbackPromptDto.FeedbackDto;
import app.habit.dto.openaidto.HabitPreQuestionRs;
import app.habit.dto.openaidto.PhaseAnswerRq;
import app.habit.dto.openaidto.PhaseEvaluationAnswerRq;
import app.habit.dto.openaidto.PhaseEvaluationRq;
import app.habit.dto.openaidto.PhaseEvaluationRs;
import app.habit.dto.openaidto.PhaseFeedbackRq;
import app.habit.dto.openaidto.PhaseFeedbackRs;
import app.habit.dto.openaidto.PhaseQuestionRs;
import app.habit.dto.openaidto.QuestionRs;
import app.habit.dto.openaidto.SingleEvaluationPromptDto;
import app.habit.dto.openaidto.UserHabitPreQuestionRq;
import app.habit.dto.openaidto.UserHabitPreQuestionRs;
import app.habit.repository.FeedbackModuleRepository;
import app.habit.repository.FeedbackSessionRepository;
import app.habit.repository.HabitAssessmentManagerRepository;
import app.habit.repository.HabitFormingPhaseRepository;
import app.habit.repository.HabitRepository;
import app.habit.service.gpt.coach.EvaluationGptCoach;
import app.habit.service.gpt.coach.FeedbackGptCoach;
import app.habit.service.gpt.coach.PhaseGptCoach;
import app.habit.service.gpt.coach.PreQuestionGptCoach;
import app.habit.service.gpt.request.RequestPrompt;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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

    @MockBean
    private HabitAssessmentManagerRepository habitAssessmentManagerRepository;

    @MockBean
    private AnswerService answerService;

    @MockBean
    private EvaluationPromptFactory evaluationPromptFactory;

    @MockBean
    private EvaluationGptCoach evaluationGptCoach;

    @MockBean
    private SpecificPhasePreQuestionPromptFactory specificPhasePreQuestionPromptFactory;

    @MockBean
    private HabitFormingPhaseRepository habitFormingPhaseRepository;

    @MockBean
    private PhaseGptCoach phaseGptCoach;

    @MockBean
    private FeedbackModuleService feedbackModuleService;

    @MockBean
    private FeedbackSessionService feedbackSessionService;

    @MockBean
    private FeedbackModuleRepository feedbackModuleRepository;

    @MockBean
    private FeedbackSessionRepository feedbackSessionRepository;

    @MockBean
    private FeedbackPromptFactory feedbackPromptFactory;

    @MockBean
    private FeedbackGptCoach feedbackGptCoach;

    @Autowired
    private OpenAiService openAiService;

    private String testUrl;

    @Captor
    private ArgumentCaptor<FeedbackPromptDto> feedbackPromptDtoCaptor;

    @BeforeEach
    void setUp() {
        testUrl = "https://api.openai.com/v1/chat/completions";
    }

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

        List<HabitPreQuestionRs> result = openAiService.getHabitPreQuestions(givenHabitId).get();

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

    @Test
    @DisplayName("사용자의 습관 형성 단계를 평가한 후 단계를 가져온다.")
    public void evaluate_habit_phase() throws Exception {
        // given
        Long givenHabitAssessmentManagerId = 1L;
        PhaseEvaluationRq rq = createPhaseEvaluationRq(givenHabitAssessmentManagerId);
        Long expectedAnswerId = 1L;
        List<SingleEvaluationPromptDto> expectedSingleEvaluationPromptDtos = createExpectedSingleEvaluationPromptDtos();
        RequestPrompt requestPrompt = new RequestPrompt();
        PhaseEvaluationRs expectedPhaseEvaluationRs = new PhaseEvaluationRs("consideration stage", "phase description");

        // when
        when(habitAssessmentManagerRepository.findById(givenHabitAssessmentManagerId)).thenReturn(
                Optional.of(new HabitAssessmentManager(givenHabitAssessmentManagerId)));
        when(answerService.save(anyString(), anyString(), eq(givenHabitAssessmentManagerId))).thenReturn(
                expectedAnswerId);
        when(subjectService.getTotalEvaluationPromptDto(givenHabitAssessmentManagerId)).thenReturn(
                expectedSingleEvaluationPromptDtos);
        when(evaluationPromptFactory.create(expectedSingleEvaluationPromptDtos)).thenReturn(requestPrompt);
        when(evaluationGptCoach.requestEvaluation(requestPrompt, testUrl)).thenReturn(CompletableFuture.completedFuture(
                expectedPhaseEvaluationRs));

        PhaseEvaluationRs result = openAiService.evaluateHabitPhase(rq).get();

        // then
        assertThat(result).isEqualTo(expectedPhaseEvaluationRs);

        verify(habitAssessmentManagerRepository, times(1)).findById(givenHabitAssessmentManagerId);
        verify(answerService, times(3)).save(anyString(), anyString(), eq(expectedAnswerId));
        verify(subjectService, times(1)).getTotalEvaluationPromptDto(givenHabitAssessmentManagerId);
        verify(evaluationPromptFactory, times(1)).create(expectedSingleEvaluationPromptDtos);
        verify(evaluationGptCoach, times(1)).requestEvaluation(requestPrompt, testUrl);
        verify(habitAssessmentManagerService, times(1)).savePhaseInfo(expectedPhaseEvaluationRs.getPhaseType(),
                expectedPhaseEvaluationRs.getPhaseDescription(), givenHabitAssessmentManagerId);
    }

    private PhaseEvaluationRq createPhaseEvaluationRq(Long habitAssessmentManagerId) {
        return new PhaseEvaluationRq(habitAssessmentManagerId,
                List.of(new PhaseEvaluationAnswerRq("key1", "userAnswer1"),
                        new PhaseEvaluationAnswerRq("key2", "userAnswer2"),
                        new PhaseEvaluationAnswerRq("key3", "userAnswer3")));
    }

    private List<SingleEvaluationPromptDto> createExpectedSingleEvaluationPromptDtos() {
        return List.of(new SingleEvaluationPromptDto("subjectKey1", "subject1", "question1", "answer1"),
                new SingleEvaluationPromptDto("subjectKey2", "subject2", "question2", "answer2"),
                new SingleEvaluationPromptDto("subjectKey3", "subject3", "question3", "answer3"));
    }

    @Test
    @DisplayName("사용자의 습관 형성 단계 향상을 위한 설문지를 가져온다")
    public void get_specific_phase_pre_questions() throws Exception {
        // given
        Long givenHabitFormingPhaseId = 1L;
        String habitFormingPhaseType = "consideration stage";
        UserHabitPreQuestionRq rq = new UserHabitPreQuestionRq(givenHabitFormingPhaseId, habitFormingPhaseType);
        RequestPrompt requestPrompt = new RequestPrompt();
        List<UserHabitPreQuestionRs> expectedUserHabitPreQuestionRsList = createUserHabitPreQuestionRsList();

        // when
        when(specificPhasePreQuestionPromptFactory.create(rq.getHabitFormingPhaseType())).thenReturn(requestPrompt);
        when(habitFormingPhaseRepository.findById(givenHabitFormingPhaseId)).thenReturn(
                Optional.of(new HabitFormingPhase(givenHabitFormingPhaseId)));
        when(phaseGptCoach.requestUserHabitPreQuestions(requestPrompt, testUrl)).thenReturn(
                CompletableFuture.completedFuture(expectedUserHabitPreQuestionRsList));
        when(feedbackModuleService.save(anyString(), anyString(), eq(givenHabitFormingPhaseId))).thenReturn(anyLong());

        List<UserHabitPreQuestionRs> result = openAiService.getSpecificPhasePreQuestions(rq).get();

        // then
        assertThat(result).isEqualTo(expectedUserHabitPreQuestionRsList);

        verify(specificPhasePreQuestionPromptFactory, times(1)).create(habitFormingPhaseType);
        verify(habitFormingPhaseRepository, times(1)).findById(givenHabitFormingPhaseId);
        verify(phaseGptCoach, times(1)).requestUserHabitPreQuestions(requestPrompt, testUrl);
        verify(feedbackModuleService, times(3)).save(anyString(), anyString(), eq(givenHabitFormingPhaseId));
        verify(feedbackSessionService, times(9)).save(anyString(), anyString(), anyLong());
    }

    private List<UserHabitPreQuestionRs> createUserHabitPreQuestionRsList() {
        return List.of(
                new UserHabitPreQuestionRs("key1", "subject1", createPhaseQuestionRsList()),
                new UserHabitPreQuestionRs("key2", "subject2", createPhaseQuestionRsList()),
                new UserHabitPreQuestionRs("key3", "subject3", createPhaseQuestionRsList()));
    }

    private List<PhaseQuestionRs> createPhaseQuestionRsList() {
        return List.of(createPhaseQuestionRs("questionKey1", "question1"),
                createPhaseQuestionRs("questionKey2", "question2"),
                createPhaseQuestionRs("questionKey3", "question3"));
    }

    private PhaseQuestionRs createPhaseQuestionRs(String questionKey, String question) {
        return new PhaseQuestionRs(questionKey, question);
    }

    @Test
    @DisplayName("습관 형성 단계 향상을 위한 질문 답변에 대한 피드백을 가져온다.")
    public void get_feedback_about_specific_subject() throws Exception {
        // given
        Long givenFeedbackModuleId = 1L;
        String givenHabitFormingPhaseType = "consideration stage";
        PhaseFeedbackRq rq = createPhaseFeedbackRq(givenHabitFormingPhaseType, givenFeedbackModuleId);
        String givenSubject = "subject1";
        FeedbackPromptDto givenFeedbackPromptDto = createFeedbackPromptDto(givenSubject);
        RequestPrompt requestPrompt = new RequestPrompt();
        PhaseFeedbackRs expectedPhaseFeedbackRs = new PhaseFeedbackRs(givenSubject, "feedback");

        // when
        when(feedbackModuleRepository.findById(eq(givenFeedbackModuleId))).thenReturn(
                Optional.of(new FeedbackModule(givenFeedbackModuleId, givenSubject)));
        when(feedbackSessionRepository.findFeedbackSessionsByFeedbackModuleId(eq(givenFeedbackModuleId))).thenReturn(
                createExpectedFeedbackSessions());
        when(feedbackPromptFactory.create(feedbackPromptDtoCaptor.capture(), eq(givenHabitFormingPhaseType))).thenReturn(
                requestPrompt);
        when(feedbackGptCoach.requestPhaseFeedback(requestPrompt, testUrl)).thenReturn(
                CompletableFuture.completedFuture(expectedPhaseFeedbackRs));

        PhaseFeedbackRs result = openAiService.getFeedbackAboutSpecificSubject(rq).get();

        // then
        assertThat(result).isEqualTo(expectedPhaseFeedbackRs);

        verify(feedbackModuleRepository, times(1)).findById(givenFeedbackModuleId);
        verify(feedbackSessionRepository, times(1)).findFeedbackSessionsByFeedbackModuleId(givenFeedbackModuleId);
        verify(feedbackPromptFactory, times(1)).create(any(FeedbackPromptDto.class), eq(givenHabitFormingPhaseType));
        verify(feedbackGptCoach, times(1)).requestPhaseFeedback(requestPrompt, testUrl);

        FeedbackPromptDto capturedFeedbackPromptDto = feedbackPromptDtoCaptor.getValue();
        assertThat(capturedFeedbackPromptDto).isEqualTo(givenFeedbackPromptDto);
    }

    private PhaseFeedbackRq createPhaseFeedbackRq(String habitFormingPhaseType, Long feedbackModuleId) {
        return new PhaseFeedbackRq(habitFormingPhaseType, feedbackModuleId,
                List.of(
                        new PhaseAnswerRq("sessionKey1", "answer1"),
                        new PhaseAnswerRq("sessionKey2", "answer2"),
                        new PhaseAnswerRq("sessionKey3", "answer3"))
        );
    }

    private FeedbackPromptDto createFeedbackPromptDto(String subject) {
        return new FeedbackPromptDto(subject,
                List.of(new FeedbackDto("sessionKey1", "question1", "answer1"),
                        new FeedbackDto("sessionKey2", "question2", "answer2"),
                        new FeedbackDto("sessionKey3", "question3", "answer3")));
    }

    private List<FeedbackSession> createExpectedFeedbackSessions() {
        return List.of(new FeedbackSession("sessionKey1", "question1", "answer1"),
                new FeedbackSession("sessionKey2", "question2", "answer2"),
                new FeedbackSession("sessionKey3", "question3", "answer3"));
    }
}
