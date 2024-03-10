package app.habit.controller;

import static app.habit.controller.Path.PHASE;
import static app.habit.controller.Path.PHASE_QUESTION;
import static app.habit.controller.Path.PRE_QUESTIONS;

import app.habit.dto.HabitPreQuestionRs;
import app.habit.dto.PhaseEvaluationRq;
import app.habit.dto.PhaseEvaluationRs;
import app.habit.dto.PhaseFeedbackRq;
import app.habit.dto.PhaseFeedbackRs;
import app.habit.dto.PhaseQuestionRs;
import app.habit.dto.UserHabitPreQuestionRq;
import app.habit.dto.UserHabitPreQuestionRs;
import app.habit.service.gpt.OpenAiService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HabitController {

    private final OpenAiService openAiService;

    @GetMapping(PRE_QUESTIONS)
    public ResponseEntity<List<HabitPreQuestionRs>> getPreQuestionsForEvaluation(
            @PathVariable("phaseId") Long phaseId) {
        List<HabitPreQuestionRs> rs = openAiService.getHabitPreQuestions(phaseId, "사전 질문", "");
        return ResponseEntity.ok().body(rs);
    }

    @PostMapping(PHASE)
    public PhaseEvaluationRs evaluatePhase(@RequestBody PhaseEvaluationRq rq) {
        return openAiService.evaluateHabitPhase(rq);
    }

    @PostMapping(PHASE_QUESTION)
    public List<UserHabitPreQuestionRs> getUserPhasePreQuestions(
            @RequestBody UserHabitPreQuestionRq rq) {
        return openAiService.getSpecificPhasePreQuestions(rq);
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

    @PostMapping("/habit/coach/phase/feedback")
    public PhaseFeedbackRs feedbackUserAnswer(@RequestBody PhaseFeedbackRq rq) {
        return openAiService.getFeedbackAboutSpecificSubject(rq);
    }

    private PhaseFeedbackRs createPhaseFeedbackRs() {
        return new PhaseFeedbackRs("일상에 통합하는 작은 단계 설정",
                "매일 아침 스트레칭은 좋은 시작입니다. 이를 일상에 통합하기 위해, 저녁에는 다음 날의 준비를 해두세요. 이러한 준비는 스트레칭을 더욱 쉽게 만들 것입니다.");
    }
}
