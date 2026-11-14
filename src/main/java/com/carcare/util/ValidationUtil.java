package com.carcare.util;

import java.util.regex.Pattern;

/**
 * Tiện ích kiểm tra tính hợp lệ dữ liệu
 */
public class ValidationUtil {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^0[0-9]{9,10}$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9_.]{4,30}$");

    public static boolean isValidEmail(String email) {
        return email != null && email.length() <= 100 && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /** Tên đăng nhập: 4-30 ký tự gồm chữ, số, dấu gạch dưới hoặc dấu chấm. */
    public static boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }

    /** Mật khẩu: 8-72 ký tự (giới hạn BCrypt), có ít nhất một chữ cái và một chữ số. */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8 || password.length() > 72) {
            return false;
        }
        boolean letter = false;
        boolean digit = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) letter = true;
            if (Character.isDigit(c)) digit = true;
        }
        return letter && digit;
    }
}
