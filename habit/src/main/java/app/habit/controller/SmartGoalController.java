package app.habit.controller;

import app.habit.dto.smartdto.CreateSmartGoalRq;
import app.habit.dto.smartdto.CreateSmartGoalRs;
import app.habit.dto.smartdto.UpdateSmartGoalRq;
import app.habit.dto.smartdto.UpdateSmartGoalRs;
import app.habit.dto.smartdto.UserSmartGoalRs;
import app.habit.service.SmartGoalService;
import java.util.concurrent.CompletableFuture;
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
public class SmartGoalController {

    private final SmartGoalService smartGoalService;

    @PostMapping("/habit/smart")
    public CompletableFuture<CreateSmartGoalRs> createSmartGoal(@RequestBody CreateSmartGoalRq rq) {
        return smartGoalService.createSmartGoal(rq);
    }

    @GetMapping("/habit/smart/{smartId}")
    public UserSmartGoalRs getSmartGoal(@PathVariable("smartId") Long smartGoalId) {
        return smartGoalService.getSmartGoal(smartGoalId);
    }

    @DeleteMapping("/habit/smart/{smartId}")
    public void deleteSmartGoal(@PathVariable("smartId") Long smartGoalId) {
        smartGoalService.deleteSmartGoal(smartGoalId);
    }

    @PutMapping("/habit/smart")
    public CompletableFuture<UpdateSmartGoalRs> updateSmartGoal(@RequestBody UpdateSmartGoalRq rq) {
        return smartGoalService.updateSmartGoal(rq);
    }
}
