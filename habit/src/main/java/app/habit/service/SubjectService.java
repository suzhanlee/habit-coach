package app.habit.service;

import app.habit.domain.Subject;
import app.habit.dto.openaidto.SingleEvaluationPromptDto;
import app.habit.repository.SubjectRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public Long save(String subjectKey, String subjectContent, Long habitAssessmentManagerId) {
        Subject subject = new Subject(subjectKey, subjectContent);
        subjectRepository.save(subject);
        subject.addHabitAssessmentManagerId(habitAssessmentManagerId);
        return subject.getId();
    }

    public List<SingleEvaluationPromptDto> getTotalEvaluationPromptDto(Long habitAssessmentManagerId) {
        List<String> subjectKeys = subjectRepository.findSubjectIdsByAssessmentManagerId(
                habitAssessmentManagerId);
        List<SingleEvaluationPromptDto> totalEvaluationPromptDto = subjectKeys.stream()
                        .map(subjectRepository::findPromptDtoBySubjectKey)
                        .toList();
        return totalEvaluationPromptDto;
    }
}
