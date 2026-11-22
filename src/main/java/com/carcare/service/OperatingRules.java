package com.carcare.service;

import java.time.*;

/** Shared validation for workshop hours and technician shifts. */
public final class OperatingRules {
    private OperatingRules() {}
    public static void hours(LocalTime opening, LocalTime closing, int buffer, int interval) {
        if (opening == null || closing == null || !opening.isBefore(closing))
            throw new IllegalArgumentException("Giờ mở cửa phải trước giờ đóng cửa (ca trong ngày).");
        long minutes = Duration.between(opening, closing).toMinutes();
        if (buffer < 0 || buffer > 240 || buffer >= minutes)
            throw new IllegalArgumentException("Thời gian đệm phải từ 0 đến 240 phút và nhỏ hơn thời gian mở cửa.");
        if (interval < 5 || interval > 120 || interval > minutes)
            throw new IllegalArgumentException("Bước khung giờ phải từ 5 đến 120 phút và không vượt thời gian mở cửa.");
    }
    public static void shift(LocalTime start, LocalTime end, LocalTime opening, LocalTime closing, boolean closed) {
        if (closed) throw new IllegalArgumentException("Ngày đã chọn là ngày nghỉ của xưởng.");
        if (start == null || end == null || !start.isBefore(end))
            throw new IllegalArgumentException("Giờ bắt đầu ca phải trước giờ kết thúc.");
        if (start.isBefore(opening) || end.isAfter(closing))
            throw new IllegalArgumentException("Ca làm việc phải nằm trong giờ mở cửa của xưởng.");
    }
    public static boolean overlaps(LocalTime start, LocalTime end, LocalTime otherStart, LocalTime otherEnd) {
        return start.isBefore(otherEnd) && end.isAfter(otherStart);
    }
}
