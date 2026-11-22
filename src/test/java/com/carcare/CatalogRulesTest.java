package com.carcare;

import com.carcare.service.CatalogRules;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CatalogRulesTest {
    @Test void validatesCurrencyWithoutSilentRounding() {
        assertEquals(new BigDecimal("150000.50"), CatalogRules.price("150000.50"));
        assertEquals(BigDecimal.ZERO, CatalogRules.price("0"));
        assertThrows(IllegalArgumentException.class, () -> CatalogRules.price("-1"));
        assertThrows(IllegalArgumentException.class, () -> CatalogRules.price("10.005"));
        assertThrows(IllegalArgumentException.class, () -> CatalogRules.price("10000000000"));
        assertThrows(IllegalArgumentException.class, () -> CatalogRules.price("NaN"));
    }
    @Test void enforcesDurationRangeAndWholeMinutes() {
        assertEquals(1, CatalogRules.duration("1"));
        assertEquals(1440, CatalogRules.duration("1440"));
        assertThrows(IllegalArgumentException.class, () -> CatalogRules.duration("0"));
        assertThrows(IllegalArgumentException.class, () -> CatalogRules.duration("1441"));
        assertThrows(IllegalArgumentException.class, () -> CatalogRules.duration("1.5"));
    }
    @Test void validatesRequiredNamesAndDatabaseLimits() {
        assertEquals("Rửa xe", CatalogRules.text(" Rửa xe ", "Tên", 100));
        assertThrows(IllegalArgumentException.class, () -> CatalogRules.text("  ", "Tên", 100));
        assertThrows(IllegalArgumentException.class, () -> CatalogRules.text("x".repeat(101), "Tên", 100));
    }
    @Test void rejectsEmptyAndReversedMaintenanceWindows() {
        LocalDateTime start = LocalDateTime.of(2027, 1, 1, 8, 0);
        assertDoesNotThrow(() -> CatalogRules.maintenance(start, start.plusHours(2)));
        assertThrows(IllegalArgumentException.class, () -> CatalogRules.maintenance(start, start));
        assertThrows(IllegalArgumentException.class, () -> CatalogRules.maintenance(start, start.minusHours(1)));
    }
}
