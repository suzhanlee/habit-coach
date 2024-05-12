package app.habit.service;

import app.habit.domain.Question;
import app.habit.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionService {

    private final QuestionRepository questionRepository;

    public Long save(String questionKey, String questionContent, Long subjectId) {
        Question question = new Question(questionKey, questionContent);
        questionRepository.save(question);
        question.addSubjectId(subjectId);
        return question.getId();
    }
}
