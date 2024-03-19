package app.habit.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class HabitFormingPhase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "habit_forming_phase_id")
    private Long id;

    @Getter
    @JoinColumn(name = "habit_assessment_manager_id")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private HabitAssessmentManager habitAssessmentManager;

    @OneToMany(mappedBy = "habitFormingPhase",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedbackModule> feedbackModules = new ArrayList<>();

    public HabitFormingPhase(Long id) {
        this.id = id;
    }

    public HabitFormingPhase(HabitAssessmentManager habitAssessmentManager) {
        this.habitAssessmentManager = habitAssessmentManager;
    }

    public void addHabitAssessmentManager(HabitAssessmentManager habitAssessmentManager) {
        this.habitAssessmentManager = habitAssessmentManager;
    }

    public void addFeedbackModules(List<FeedbackModule> feedbackModules) {
        this.feedbackModules.addAll(feedbackModules);
        feedbackModules.forEach(feedbackModule -> feedbackModule.addHabitFormingPhase(this));
    }

    public void addAnswers(List<Answer> answers) {
        this.habitAssessmentManager.addAnswersAccordingToSubject(answers);
    }

    public String createPrompt() {
        return this.habitAssessmentManager.createPrompt();
    }

    public void addEvaluationResult(String phaseType, String phaseDescription) {
        this.habitAssessmentManager.assess(phaseType, phaseDescription);
    }
}
