package app.habit.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Smart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "smart_id")
    private Long id;

    @Column(name = "goal_tracker_id")
    private Long goalTrackerId;

    private String goal;
    private String feedback;

    public Smart(Long goalTrackerId, String goal, String feedback) {
        this.goalTrackerId = goalTrackerId;
        this.goal = goal;
        this.feedback = feedback;
    }

    public Smart(Long id, Long goalTrackerId) {
        this.id = id;
        this.goalTrackerId = goalTrackerId;
    }
}
