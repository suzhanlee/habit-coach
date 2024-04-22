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
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
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
    public CompletableFuture<List<HabitPreQuestionRs>> getPreQuestionsForEvaluation(
            @PathVariable("phaseId") Long phaseId) {
        System.out.println("OpenAiController.getPreQuestionsForEvaluation1 : " + Thread.currentThread());
        CompletableFuture<List<HabitPreQuestionRs>> 사전_질문 = openAiService.getHabitPreQuestions(phaseId, "사전 질문", "");
        System.out.println("OpenAiController.getPreQuestionsForEvaluation2 : "+ Thread.currentThread());
        return 사전_질문;
    }

    @PostMapping("/habit/coach/phase")
    public CompletableFuture<PhaseEvaluationRs> evaluatePhase(@RequestBody PhaseEvaluationRq rq) {
        return openAiService.evaluateHabitPhase(rq);
    }

    @PostMapping("/habit/coach/phase/question")
    public CompletableFuture<List<UserHabitPreQuestionRs>> getUserPhasePreQuestions(@RequestBody UserHabitPreQuestionRq rq) {
        return openAiService.getSpecificPhasePreQuestions(rq);
    }

    @PostMapping("/habit/coach/phase/feedback")
    public CompletableFuture<PhaseFeedbackRs> feedbackUserAnswer(@RequestBody PhaseFeedbackRq rq) {
        return openAiService.getFeedbackAboutSpecificSubject(rq);
    }
}
