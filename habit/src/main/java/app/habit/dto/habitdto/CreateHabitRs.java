package app.habit.dto.habitdto;

import app.habit.domain.GoalTracker;
import app.habit.domain.Habit;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateHabitRs {

    private long habitId;

    public CreateHabitRs(Habit habit) {
        this.habitId = habit.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CreateHabitRs that = (CreateHabitRs) o;
        return habitId == that.habitId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(habitId);
    }
}
