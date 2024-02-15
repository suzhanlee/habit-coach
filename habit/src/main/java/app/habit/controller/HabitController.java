package app.habit.controller;

import static app.habit.controller.Path.PRE_QUESTIONS;

import app.habit.dto.HabitPreQuestionRs;
import app.habit.dto.QuestionRs;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
}
