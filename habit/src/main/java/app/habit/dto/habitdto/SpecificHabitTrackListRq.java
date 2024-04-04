package app.habit.dto.habitdto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SpecificHabitTrackListRq {

    private long goalTrackerId;
    private int year;
    private int month;
}
