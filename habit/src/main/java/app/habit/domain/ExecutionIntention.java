package app.habit.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class ExecutionIntention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "execution_intention_id")
    private Long id;

    @Column(name = "goal_tracker_id")
    private Long goalTrackerId;
}
