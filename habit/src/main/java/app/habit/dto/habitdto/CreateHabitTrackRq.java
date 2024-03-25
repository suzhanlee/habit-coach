package app.habit.dto.habitdto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateHabitTrackRq {

    private long habitId;
    private LocalDate trackDateTime;
    private String trackType;
}
