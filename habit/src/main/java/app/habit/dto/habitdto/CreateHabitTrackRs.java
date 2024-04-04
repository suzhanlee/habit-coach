package app.habit.dto.habitdto;

import app.habit.domain.Track;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateHabitTrackRs {

    private long goalTrackerId;
    private long trackId;

    public CreateHabitTrackRs(Long goalTrackerId, Track track) {
        this.goalTrackerId = goalTrackerId;
        this.trackId = track.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CreateHabitTrackRs that = (CreateHabitTrackRs) o;
        return goalTrackerId == that.goalTrackerId && trackId == that.trackId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(goalTrackerId, trackId);
    }
}
