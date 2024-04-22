package app.habit.repository;

import app.habit.domain.Subject;
import app.habit.dto.openaidto.SingleEvaluationPromptDto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    @Query("SELECT s FROM Subject s WHERE s.habitAssessmentManagerId = :habitAssessmentManagerId AND s.subjectKey = :key")
    Optional<Subject> findByKey(@Param("habitAssessmentManagerId") Long habitAssessmentManagerId,
                                @Param("key") String key);

    @Query("SELECT new app.habit.dto.openaidto.SingleEvaluationPromptDto(" +
            "s.subjectKey, " +
            "s.subject, " +
            "q.question, " +
            "a.answer )" +
            "FROM Subject s " +
            "LEFT JOIN Question q ON s.id = q.subjectId LEFT JOIN Answer a ON s.id = a.subjectId WHERE s.subjectKey = :subjectKey")
    SingleEvaluationPromptDto findPromptDtoBySubjectKey(@Param("subjectKey") String subjectKey);

    @Query("SELECT s.subjectKey from Subject s WHERE s.habitAssessmentManagerId = :habitAssessmentManagerId")
    List<String> findSubjectIdsByAssessmentManagerId(@Param("habitAssessmentManagerId") Long habitAssessmentManagerId);
}
