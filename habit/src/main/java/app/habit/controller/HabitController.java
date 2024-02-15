package app.habit.controller;

import static app.habit.controller.Path.PHASE;
import static app.habit.controller.Path.PHASE_QUESTION;
import static app.habit.controller.Path.PRE_QUESTIONS;
import static app.habit.domain.HabitFormingPhaseType.CONSIDERATION_STAGE;
import static org.springframework.http.HttpStatus.CREATED;

import app.habit.dto.HabitPreQuestionRs;
import app.habit.dto.PhaseEvaluationRq;
import app.habit.dto.PhaseEvaluationRs;
import app.habit.dto.PhaseQuestionRs;
import app.habit.dto.QuestionRs;
import app.habit.dto.UserHabitPreQuestionRq;
import app.habit.dto.UserHabitPreQuestionRs;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HabitController {

    @GetMapping(PRE_QUESTIONS)
    public ResponseEntity<List<HabitPreQuestionRs>> getPreQuestionsForEvaluation(
            @PathVariable("phaseId") Long phaseId) {
        return ResponseEntity.ok().body(createPreQuestions());
    }

    private List<HabitPreQuestionRs> createPreQuestions() {
        HabitPreQuestionRs rs1 = createHabitPreQuestionRs("1", "일반적인 인식 및 의도", "1",
                "현재 어떤 습관을 기르기 위해 노력하고 있으며, 그것이 왜 중요하다고 생각하시나요?");
        HabitPreQuestionRs rs2 = createHabitPreQuestionRs("2", "목표 명확성 및 계획", "2",
                "이 습관과 관련된 구체적인 목표를 설정하셨나요? 이러한 목표를 달성하기 위해 어떤 단계를 계획했나요?");
        HabitPreQuestionRs rs3 = createHabitPreQuestionRs("3", "초기 조치 및 작은 단계", "3",
                "이 습관을 시작하기 위해 취했거나 취할 계획인 가장 작은 조치는 무엇입니까?");
        HabitPreQuestionRs rs4 = createHabitPreQuestionRs("4", "일관성과 실천", "4",
                "이 습관을 얼마나 일관되게 수행할 수 있었으며 진행 상황을 어떻게 모니터링합니까?");
        HabitPreQuestionRs rs5 = createHabitPreQuestionRs("5", "유지관리 및 라이프스타일 통합", "5",
                "시간이 지나도 이 습관을 유지하는 데 어려움을 겪은 적이 있습니까? 당신은 그들을 어떻게 처리했는가?");

        return new ArrayList<>(List.of(rs1, rs2, rs3, rs4, rs5));
    }

    private HabitPreQuestionRs createHabitPreQuestionRs(String subjectKey, String subject, String questionKey,
                                                        String question) {
        return new HabitPreQuestionRs(subjectKey, subject,
                new QuestionRs(questionKey, question));
    }

    @PostMapping(PHASE)
    public ResponseEntity<PhaseEvaluationRs> evaluatePhase(@RequestBody PhaseEvaluationRq rq) {
        return ResponseEntity.status(CREATED).body(createExpectedPhaseEvaluationRs());
    }

    private PhaseEvaluationRs createExpectedPhaseEvaluationRs() {
        return new PhaseEvaluationRs(1, CONSIDERATION_STAGE,
                "목표 설정 및 계획의 고려 단계에서 개인은 단순히 변화를 만들거나 새로운 습관을 개발하는 것에 대해 생각하는 것에서 이를 수행하는 방법을 적극적으로 계획하는 것으로 전환하고 있습니다. 이 단계에는 모호한 아이디어나 욕구를 구체적이고 실행 가능한 목표로 구체화하는 작업이 포함됩니다. 이는 '변화를 만들고 싶다'에서 '정확히 내가 할 일과 방법은 다음과 같습니다.'로 이동하는 것입니다. 초점은 구체성, 타당성 및 앞으로의 여정에 대한 계획에 있습니다.");
    }

    @PostMapping(PHASE_QUESTION)
    public ResponseEntity<List<UserHabitPreQuestionRs>> getUserPhasePreQuestions(@RequestBody UserHabitPreQuestionRq rq) {
        return ResponseEntity.ok().body(createUserHabitPreQuestionRs());
    }

    private List<UserHabitPreQuestionRs> createUserHabitPreQuestionRs() {
        UserHabitPreQuestionRs rs1 = new UserHabitPreQuestionRs(1, "1", "자원과 지원에 대한 질문들",
                createPhaseFirstModuleQuestion());
        UserHabitPreQuestionRs rs2 = new UserHabitPreQuestionRs(2, "2", "자원과 지원에 대한 질문들",
                createPhaseSecondModuleQuestion());
        UserHabitPreQuestionRs rs3 = new UserHabitPreQuestionRs(3, "3", "초기 성공 및 동기부여에 대한 질문들",
                createPhaseThirdModuleQuestion());

        return new ArrayList<>(List.of(rs1, rs2, rs3));
    }

    private List<PhaseQuestionRs> createPhaseFirstModuleQuestion() {
        PhaseQuestionRs rs1 = new PhaseQuestionRs("1", "이 작은 단계들을 실천하기 위해 필요한 자원은 무엇인가요?");
        PhaseQuestionRs rs2 = new PhaseQuestionRs("2", "작은 단계들을 수행하는 데 있어서 가장 큰 동기는 무엇인가요?");
        PhaseQuestionRs rs3 = new PhaseQuestionRs("3", "이 작은 단계들을 어떻게 일상의 다른 활동이나 습관과 연결할 수 있을까요?");

        return new ArrayList<>(List.of(rs1, rs2, rs3));
    }

    private List<PhaseQuestionRs> createPhaseSecondModuleQuestion() {
        PhaseQuestionRs rs1 = new PhaseQuestionRs("1", "이 작은 단계들을 실천하기 위해 필요한 자원은 무엇인가요?");
        PhaseQuestionRs rs2 = new PhaseQuestionRs("2", "이 목표를 달성하는 데 도움이 될 수 있는 외부의 지원이나 자원은 무엇인가요?");
        PhaseQuestionRs rs3 = new PhaseQuestionRs("3", "이 작은 단계들을 지속적으로 유지하기 위해 필요한 것은 무엇이라고 생각하나요?");

        return new ArrayList<>(List.of(rs1, rs2, rs3));
    }

    private List<PhaseQuestionRs> createPhaseThirdModuleQuestion() {
        PhaseQuestionRs rs1 = new PhaseQuestionRs("1", "이 작은 단계들을 성공적으로 수행했을 때 자신에게 어떻게 보상하거나 칭찬할 계획인가요?");
        PhaseQuestionRs rs2 = new PhaseQuestionRs("2", "작은 단계들을 수행하는 데 있어서 가장 큰 동기는 무엇인가요?");
        PhaseQuestionRs rs3 = new PhaseQuestionRs("3", "이 작은 단계들을 어떻게 일상의 다른 활동이나 습관과 연결할 수 있을까요?");

        return new ArrayList<>(List.of(rs1, rs2, rs3));
    }
}
