package app.habit.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class HabitFormingPhase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "habit_forming_phase_id")
    @Getter
    private Long id;

    @Column(name = "habit_id")
    @Getter
    private Long habitId;

    public HabitFormingPhase(Long id) {
        this.id = id;
    }

    public void addHabitId(Long habitFormingPhaseId) {
        this.habitId = habitFormingPhaseId;
    }

    public void setId(long id) {
        this.id = id;
    }
}
