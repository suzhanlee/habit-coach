package app.habit.controller;

import app.habit.dto.executionintentiondto.CreateExecutionIntentionRq;
import app.habit.dto.executionintentiondto.CreateExecutionIntentionRs;
import app.habit.dto.executionintentiondto.UpdateExecutionIntentionRq;
import app.habit.dto.executionintentiondto.UpdateExecutionIntentionRs;
import app.habit.service.ExecutionIntentionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ExecutionIntentionController {

    private final ExecutionIntentionService executionIntentionService;

    @GetMapping("/habit/execution-intention/{executionIntentionId}")
    public void getExecutionIntention(@PathVariable("executionIntentionId") Long executionIntentionId) {
        executionIntentionService.getExecutionIntention(executionIntentionId);
    }
}
