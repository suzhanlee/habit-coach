package app.habit.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Habit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "habit_id")
    private Long id;

    private String name;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private HabitFormingPhase habitFormingPhase;

    @Column(name = "user_id")
    private Long userId;

    public Habit(String habitName, long userId) {
        this.name = habitName;
        this.habitFormingPhase = new HabitFormingPhase();
        this.userId = userId;
    }

    public Habit(long id) {
        this.id = id;
    }

    public void setId(long habitId) {
        this.id = habitId;
    }

    public void addUserId(long userId) {
        this.userId = userId;
    }
}
