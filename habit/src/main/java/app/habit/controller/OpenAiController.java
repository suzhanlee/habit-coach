package app.habit.controller;

import app.habit.dto.openaidto.HabitPreQuestionRs;
import app.habit.dto.openaidto.PhaseEvaluationRq;
import app.habit.dto.openaidto.PhaseEvaluationRs;
import app.habit.dto.openaidto.PhaseFeedbackRq;
import app.habit.dto.openaidto.PhaseFeedbackRs;
import app.habit.dto.openaidto.UserHabitPreQuestionRq;
import app.habit.dto.openaidto.UserHabitPreQuestionRs;
import app.habit.service.OpenAiService;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OpenAiController {

    private final OpenAiService openAiService;

    @GetMapping("/habit/coach/phase/{phaseId}")
    public CompletableFuture<List<HabitPreQuestionRs>> getPreQuestionsForEvaluation(
            @PathVariable("phaseId") Long phaseId) {
        log.info("OpenAiController.getPreQuestionsForEvaluation 1 : " + Thread.currentThread().getName());
        CompletableFuture<List<HabitPreQuestionRs>> rs = openAiService.getHabitPreQuestions(phaseId);
        log.info("OpenAiController.getPreQuestionsForEvaluation 2 : " + Thread.currentThread().getName());
        return rs;
    }

    @PostMapping("/habit/coach/phase")
    public CompletableFuture<PhaseEvaluationRs> evaluatePhase(@RequestBody PhaseEvaluationRq rq) {
        log.info("OpenAiController.evaluatePhase 1 : " + Thread.currentThread().getName());
        CompletableFuture<PhaseEvaluationRs> rs = openAiService.evaluateHabitPhase(rq);
        log.info("OpenAiController.evaluatePhase 2 : " + Thread.currentThread().getName());
        return rs;
    }

    @PostMapping("/habit/coach/phase/question")
    public CompletableFuture<List<UserHabitPreQuestionRs>> getUserPhasePreQuestions(@RequestBody UserHabitPreQuestionRq rq) {
        log.info("OpenAiController.getUserPhasePreQuestions 1 : " + Thread.currentThread().getName());
        CompletableFuture<List<UserHabitPreQuestionRs>> rs = openAiService.getSpecificPhasePreQuestions(
                rq);
        log.info("OpenAiController.getUserPhasePreQuestions 2 : " + Thread.currentThread().getName());
        return rs;
    }

    @PostMapping("/habit/coach/phase/feedback")
    public CompletableFuture<PhaseFeedbackRs> feedbackUserAnswer(@RequestBody PhaseFeedbackRq rq) {
        log.info("OpenAiController.feedbackUserAnswer 1 : " + Thread.currentThread().getName());
        CompletableFuture<PhaseFeedbackRs> rs = openAiService.getFeedbackAboutSpecificSubject(
                rq);
        log.info("OpenAiController.feedbackUserAnswer 2 : " + Thread.currentThread().getName());
        return rs;
    }
}
