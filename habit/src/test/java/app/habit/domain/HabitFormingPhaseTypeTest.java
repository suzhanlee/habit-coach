package app.habit.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class HabitFormingPhaseTypeTest {

    @ParameterizedTest
    @DisplayName("원하는 타입을 찾는다.")
    @CsvSource(value = {"precontemplation stage:PRECONTEMPLATION_STAGE", "consideration stage:CONSIDERATION_STAGE",
            "preparation stage:PREPARATION_STAGE", "action stage:ACTION_STAGE", "maintenance stage:MAINTENANCE_STAGE"}, delimiter = ':')
    void find_type(String type, HabitFormingPhaseType expected) {
        // when
        HabitFormingPhaseType result = HabitFormingPhaseType.findType(type);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("실제 타입들에 원하는 타입이 존재하지 않으면 찾을 수 없다.")
    @Test
    void find_type_error() {
        // when //then
        assertThatThrownBy(() -> HabitFormingPhaseType.findType("normal stage"))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }
}
