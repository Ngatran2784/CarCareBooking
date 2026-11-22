package com.carcare;

import com.carcare.service.OperatingRules;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OperatingRulesTest {
    private LocalTime t(String value) { return LocalTime.parse(value); }
    @Test void acceptsValidHoursAndZeroBuffer() {
        assertDoesNotThrow(() -> OperatingRules.hours(t("07:30"),t("18:30"),0,30));
        assertDoesNotThrow(() -> OperatingRules.hours(t("07:30"),t("18:30"),15,30));
    }
    @Test void rejectsInvalidHoursAndBuffer() {
        assertThrows(IllegalArgumentException.class, () -> OperatingRules.hours(t("18:30"),t("07:30"),15,30));
        assertThrows(IllegalArgumentException.class, () -> OperatingRules.hours(t("08:00"),t("08:00"),15,30));
        assertThrows(IllegalArgumentException.class, () -> OperatingRules.hours(t("08:00"),t("09:00"),60,30));
        assertThrows(IllegalArgumentException.class, () -> OperatingRules.hours(t("08:00"),t("18:00"),-1,30));
        assertThrows(IllegalArgumentException.class, () -> OperatingRules.hours(t("08:00"),t("18:00"),241,30));
        assertThrows(IllegalArgumentException.class, () -> OperatingRules.hours(t("08:00"),t("18:00"),15,0));
    }
    @Test void acceptsShiftAtOpeningAndClosing() {
        assertDoesNotThrow(() -> OperatingRules.shift(t("07:30"),t("18:30"),t("07:30"),t("18:30"),false));
    }
    @Test void rejectsClosedDayAndOutOfHours() {
        assertThrows(IllegalArgumentException.class, () -> OperatingRules.shift(t("08:00"),t("12:00"),t("07:30"),t("18:30"),true));
        assertThrows(IllegalArgumentException.class, () -> OperatingRules.shift(t("07:00"),t("12:00"),t("07:30"),t("18:30"),false));
        assertThrows(IllegalArgumentException.class, () -> OperatingRules.shift(t("08:00"),t("19:00"),t("07:30"),t("18:30"),false));
        assertThrows(IllegalArgumentException.class, () -> OperatingRules.shift(t("12:00"),t("08:00"),t("07:30"),t("18:30"),false));
    }
    @Test void adjacentShiftsDoNotOverlap() {
        assertFalse(OperatingRules.overlaps(t("08:00"),t("12:00"),t("12:00"),t("17:00")));
        assertTrue(OperatingRules.overlaps(t("08:00"),t("12:00"),t("11:59"),t("17:00")));
        assertTrue(OperatingRules.overlaps(t("08:00"),t("17:00"),t("09:00"),t("12:00")));
    }
}
