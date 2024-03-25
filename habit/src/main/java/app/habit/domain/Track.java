package app.habit.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Track {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "track_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private TrackType trackType;

    private LocalDate trackDateTime;

    @Column(name = "goal_tracker_id")
    private Long goalTrackerId;

    public Track(String trackType, LocalDate trackDateTime, Long goalTrackerId) {
        this.trackType = TrackType.findByType(trackType);
        this.trackDateTime = trackDateTime;
        this.goalTrackerId = goalTrackerId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void addGoalTrackerId(Long goalTrackerId) {
        this.goalTrackerId = goalTrackerId;
    }
}

