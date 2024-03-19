package app.habit.domain;

import java.util.Arrays;

public enum HabitFormingPhaseType {

    PRECONTEMPLATION_STAGE("precontemplation stage"),
    CONSIDERATION_STAGE("consideration stage"),
    PREPARATION_STAGE("preparation stage"),
    ACTION_STAGE("action stage"),
    MAINTENANCE_STAGE("maintenance stage");

    private final String name;

    HabitFormingPhaseType(String name) {
        this.name = name;
    }

    public static HabitFormingPhaseType findType(String name) {
        return Arrays.stream(values())
                .filter(phaseType -> phaseType.name.equals(name))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
