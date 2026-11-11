package com.carcare;

import com.carcare.util.PasswordUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordUtilTest {
    @Test
    void hashesAndVerifiesPassword() {
        String password = "Correct-Horse-Battery-Staple";
        String hash = PasswordUtil.hashPassword(password);

        assertNotEquals(password, hash);
        assertTrue(hash.startsWith("$2"));
        assertTrue(PasswordUtil.verifyPassword(password, hash));
        assertFalse(PasswordUtil.verifyPassword("incorrect", hash));
    }

    @Test
    void rejectsMissingOrMalformedPasswordValues() {
        assertFalse(PasswordUtil.verifyPassword(null, "$2a$invalid"));
        assertFalse(PasswordUtil.verifyPassword("password", "not-a-bcrypt-hash"));
        assertFalse(PasswordUtil.verifyPassword("password", "$2a$invalid"));
    }
}
