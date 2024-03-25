package app.habit.dto.habitdto;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserHabitListRs {

    private long habitId;
    private String habitName;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserHabitListRs that = (UserHabitListRs) o;
        return habitId == that.habitId && Objects.equals(habitName, that.habitName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(habitId, habitName);
    }
}
