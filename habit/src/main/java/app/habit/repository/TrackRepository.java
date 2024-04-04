package app.habit.repository;

import app.habit.domain.Track;
import app.habit.dto.habitdto.SpecificHabitTrackListRs;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TrackRepository extends JpaRepository<Track, Long> {

    @Query("SELECT new app.habit.dto.habitdto.SpecificHabitTrackListRs(t.id, t.trackDateTime, t.trackType) " +
            "FROM Track t " +
            "WHERE t.goalTrackerId = :goalTrackerId " +
            "AND YEAR(t.trackDateTime) = :year " +
            "AND MONTH(t.trackDateTime) = :month")
    List<SpecificHabitTrackListRs> findTracksByGoalTrackerIdAndYearAndMonth(@Param("goalTrackerId") Long goalTrackerId,
                                                                            @Param("year") int year,
                                                                            @Param("month") int month);
}

