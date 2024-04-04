package app.habit.domain;

import java.util.Arrays;

public enum TrackType {

    CHECKED("checked"), UNCHECKED("unchecked");

    private final String name;

    TrackType(String name) {
        this.name = name;
    }

    public static TrackType findByType(String trackType) {
        return Arrays.stream(values())
                .filter(type -> type.name.equals(trackType))
                .findFirst()
                .orElseThrow();
    }
}

