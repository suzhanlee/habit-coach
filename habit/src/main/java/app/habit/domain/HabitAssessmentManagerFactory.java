package app.habit.domain;

import app.habit.dto.HabitPreQuestionRs;
import app.habit.dto.QuestionRs;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class HabitAssessmentManagerFactory {

    public HabitAssessmentManager createHabitAssessmentManager(List<HabitPreQuestionRs> content) {
        List<Subject> subjects = content.stream().map(this::createSubject).collect(Collectors.toList());
        return new HabitAssessmentManager(subjects);
    }

    private Subject createSubject(HabitPreQuestionRs habitPreQuestionRs) {
        String key = habitPreQuestionRs.getKey();
        String subject = habitPreQuestionRs.getSubject();
        return new Subject(key, subject, createQuestion(habitPreQuestionRs.getQuestionRs()));
    }

    private Question createQuestion(QuestionRs questionRs) {
        String questionKey = questionRs.getQuestionKey();
        String question = questionRs.getQuestion();
        return new Question(questionKey, question);
    }
}
