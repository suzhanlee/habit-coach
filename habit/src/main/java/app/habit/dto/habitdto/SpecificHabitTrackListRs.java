package app.habit.dto.habitdto;

import app.habit.domain.TrackType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SpecificHabitTrackListRs {

    private long trackId;
    private LocalDate trackDateTime;
    private TrackType trackType;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SpecificHabitTrackListRs that = (SpecificHabitTrackListRs) o;
        return trackId == that.trackId && Objects.equals(trackDateTime, that.trackDateTime)
                && trackType == that.trackType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(trackId, trackDateTime, trackType);
    }
}
