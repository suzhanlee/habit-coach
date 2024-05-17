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
public class GoalTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goal_tracker_id")
    private Long id;

    @Column(name = "habit_id", unique = false)
    private Long habitId;

    public GoalTracker(Long habitId) {
        this.habitId = habitId;
    }

    public GoalTracker(Long id, Long habitId) {
        this.id = id;
        this.habitId = habitId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void addHabitId(Long id) {
        this.habitId = habitId;
    }
}
