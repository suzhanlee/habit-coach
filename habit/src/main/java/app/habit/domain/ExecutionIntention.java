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
public class ExecutionIntention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "execution_intention_id")
    private Long id;

    @Column(name = "goal_tracker_id")
    private Long goalTrackerId;

    private String content;
    private String feedback;

    public ExecutionIntention(Long goalTrackerId, String content, String feedback) {
        this.goalTrackerId = goalTrackerId;
        this.content = content;
        this.feedback = feedback;
    }

    public ExecutionIntention(Long id, Long goalTrackerId) {
        this.id = id;
        this.goalTrackerId = goalTrackerId;
    }
}
