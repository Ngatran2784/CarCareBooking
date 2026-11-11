package com.carcare.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    // Băm mật khẩu thô thành chuỗi mã hóa BCrypt
    public static String hashPassword(String plainTextPassword) {
        if (plainTextPassword == null || plainTextPassword.isEmpty()) {
            return null;
        }
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(12));
    }

    // Kiểm tra mật khẩu thô có khớp với chuỗi hash trong DB hay không
    public static boolean verifyPassword(String plainTextPassword, String hashedPassword) {
        if (plainTextPassword == null || hashedPassword == null || !hashedPassword.startsWith("$2")) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainTextPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}