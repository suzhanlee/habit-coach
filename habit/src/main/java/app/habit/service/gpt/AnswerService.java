package app.habit.service.gpt;

import app.habit.domain.Answer;
import app.habit.domain.Subject;
import app.habit.repository.AnswerRepository;
import app.habit.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final SubjectRepository subjectRepository;

    public Long save(String answerKey, String answerContent) {
        Answer answer = new Answer(answerKey, answerContent);
        answerRepository.save(answer);
        Subject subject = subjectRepository.findByKey(answerKey).orElseThrow();
        answer.addSubjectId(subject.getId());
        return answer.getId();
    }
}
