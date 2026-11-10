package com.carcare;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

public class AuthSecurityTest {

    @Test
    @DisplayName("Kiểm tra mã hóa và xác thực mật khẩu bằng BCrypt")
    public void testBCryptHashing() {
        String rawPassword = "Admin@123";
        String hashed = BCrypt.hashpw(rawPassword, BCrypt.gensalt(10));
        
        Assertions.assertNotNull(hashed);
        Assertions.assertTrue(hashed.startsWith("$2a$") || hashed.startsWith("$2b$") || hashed.startsWith("$2y$"));
        Assertions.assertTrue(BCrypt.checkpw("Admin@123", hashed));
        Assertions.assertFalse(BCrypt.checkpw("WrongPassword", hashed));

        System.out.println("Generated Hash for Admin@123: " + hashed);
    }
}
