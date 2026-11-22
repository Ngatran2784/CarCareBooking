package com.carcare.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/** Validation shared by the service catalogue and bay maintenance forms. */
public final class CatalogRules {
    private CatalogRules() {}
    public static String text(String value, String label, int max) {
        String result = value == null ? "" : value.trim();
        if (result.isEmpty() || result.length() > max)
            throw new IllegalArgumentException(label + " phải có từ 1 đến " + max + " ký tự.");
        return result;
    }
    public static BigDecimal price(String value) {
        try {
            BigDecimal price = new BigDecimal(value);
            if (price.signum() < 0 || price.compareTo(new BigDecimal("9999999999.99")) > 0
                    || price.stripTrailingZeros().scale() > 2) throw new NumberFormatException();
            return price;
        } catch (Exception e) {
            throw new IllegalArgumentException("Giá phải từ 0 đến 9.999.999.999,99 đồng, tối đa 2 chữ số thập phân.");
        }
    }
    public static int duration(String value) {
        int minutes = id(value);
        if (minutes < 1 || minutes > 1440)
            throw new IllegalArgumentException("Thời lượng phải từ 1 đến 1440 phút.");
        return minutes;
    }
    public static int id(String value) {
        try {
            int id = Integer.parseInt(value);
            if (id < 0) throw new NumberFormatException();
            return id;
        } catch (Exception e) { throw new IllegalArgumentException("Giá trị số không hợp lệ."); }
    }
    public static String status(String value, Set<String> allowed) {
        if (value == null || !allowed.contains(value)) throw new IllegalArgumentException("Trạng thái không hợp lệ.");
        return value;
    }
    public static void maintenance(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null || !start.isBefore(end))
            throw new IllegalArgumentException("Bắt đầu bảo trì phải trước thời điểm kết thúc.");
        if (start.getYear() < 1000 || end.getYear() > 9999)
            throw new IllegalArgumentException("Ngày bảo trì phải nằm trong phạm vi năm 1000–9999.");
    }
}
