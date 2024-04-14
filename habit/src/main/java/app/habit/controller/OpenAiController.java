package app.habit.controller;

import app.habit.dto.openaidto.HabitPreQuestionRs;
import app.habit.dto.openaidto.PhaseEvaluationRq;
import app.habit.dto.openaidto.PhaseEvaluationRs;
import app.habit.dto.openaidto.PhaseFeedbackRq;
import app.habit.dto.openaidto.PhaseFeedbackRs;
import app.habit.dto.openaidto.UserHabitPreQuestionRq;
import app.habit.dto.openaidto.UserHabitPreQuestionRs;
import app.habit.service.gpt.OpenAiService;
import java.util.List;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OpenAiController {

    private final OpenAiService openAiService;

    @GetMapping("/habit/coach/phase/{phaseId}")
    public Callable<List<HabitPreQuestionRs>> getPreQuestionsForEvaluation(
            @PathVariable("phaseId") Long phaseId) {
        return () -> openAiService.getHabitPreQuestions(phaseId, "사전 질문", "");
    }

    @PostMapping("/habit/coach/phase")
    public Callable<PhaseEvaluationRs> evaluatePhase(@RequestBody PhaseEvaluationRq rq) {
        return () -> openAiService.evaluateHabitPhase(rq);
    }

    @PostMapping("/habit/coach/phase/question")
    public Callable<List<UserHabitPreQuestionRs>> getUserPhasePreQuestions(@RequestBody UserHabitPreQuestionRq rq) {
        return () -> openAiService.getSpecificPhasePreQuestions(rq);
    }

    @PostMapping("/habit/coach/phase/feedback")
    public Callable<PhaseFeedbackRs> feedbackUserAnswer(@RequestBody PhaseFeedbackRq rq) {
        return () -> openAiService.getFeedbackAboutSpecificSubject(rq);
    }
}
