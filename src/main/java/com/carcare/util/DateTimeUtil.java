package com.carcare.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Tiện ích định dạng ngày giờ chuẩn Việt Nam
 */
public class DateTimeUtil {
    public static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    public static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static String formatDateTime(LocalDateTime ldt) {
        if (ldt == null) return "";
        return ldt.format(DISPLAY_FORMATTER);
    }

    public static String formatDate(LocalDateTime ldt) {
        if (ldt == null) return "";
        return ldt.format(DATE_ONLY_FORMATTER);
    }
}
