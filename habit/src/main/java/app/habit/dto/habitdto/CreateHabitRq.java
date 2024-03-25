package app.habit.dto.habitdto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateHabitRq {

    private Long userId;
    private String habitName;
}
