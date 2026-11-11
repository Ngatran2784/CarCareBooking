package com.carcare;

import com.carcare.dal.DBContext;
import com.carcare.dal.PasswordResetDAO;
import com.carcare.dal.UserDAO;
import com.carcare.model.User;
import com.carcare.util.ValidationUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Kiểm thử Sprint 1 - mục 5: đăng ký khách hàng, token đặt lại mật khẩu dùng một lần có thời hạn,
 * và liên kết users - customers - employees.
 */
public class RegistrationPasswordResetTest {

    private static final String USERNAME = "test_reg_user";
    private static final String EMAIL = "test_reg_user@example.com";
    private static final String PHONE = "0999000111";

    private final UserDAO userDAO = new UserDAO();
    private final PasswordResetDAO resetDAO = new PasswordResetDAO();

    @BeforeEach
    @AfterEach
    public void cleanUp() throws Exception {
        try (Connection c = DBContext.getConnection()) {
            // customers.user_id dùng ON DELETE SET NULL nên xóa hồ sơ trước
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM customers WHERE phone = ?")) {
                ps.setString(1, PHONE);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM users WHERE username = ?")) {
                ps.setString(1, USERNAME);
                ps.executeUpdate();
            }
        }
    }

    @Test
    @DisplayName("Kiểm tra dữ liệu đăng ký hợp lệ / không hợp lệ")
    public void validation() {
        Assertions.assertTrue(ValidationUtil.isValidUsername("nguyen.van_a1"));
        Assertions.assertFalse(ValidationUtil.isValidUsername("ab"));
        Assertions.assertFalse(ValidationUtil.isValidUsername("co dau cach"));
        Assertions.assertTrue(ValidationUtil.isValidPassword("Abcdef12"));
        Assertions.assertFalse(ValidationUtil.isValidPassword("abcdefgh"));
        Assertions.assertFalse(ValidationUtil.isValidPassword("12345678"));
        Assertions.assertFalse(ValidationUtil.isValidPassword("Ab1"));
        Assertions.assertTrue(ValidationUtil.isValidEmail("a@b.vn"));
        Assertions.assertFalse(ValidationUtil.isValidEmail("a@b"));
        Assertions.assertTrue(ValidationUtil.isValidPhone("0901234567"));
        Assertions.assertFalse(ValidationUtil.isValidPhone("123"));
    }

    @Test
    @DisplayName("Đăng ký tạo users + user_roles + customers liên kết 1-1, mật khẩu băm BCrypt")
    public void registerLinksUserAndCustomer() throws Exception {
        int id = userDAO.registerCustomer(USERNAME, "Passw0rdOK", "Khách Thử", EMAIL, PHONE, null);
        Assertions.assertTrue(id > 0);

        User u = userDAO.authenticate(USERNAME, "Passw0rdOK");
        Assertions.assertNotNull(u);
        Assertions.assertTrue(u.hasRole("CUSTOMER"));
        Assertions.assertNotEquals("Passw0rdOK", u.getPasswordHash());

        try (Connection c = DBContext.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT customer_code FROM customers WHERE user_id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                Assertions.assertTrue(rs.next(), "Phải có hồ sơ customers liên kết với tài khoản");
                Assertions.assertTrue(rs.getString(1).startsWith("CUST-"));
            }
        }
    }

    @Test
    @DisplayName("Đăng ký trùng username/email/số điện thoại bị từ chối")
    public void duplicateRejected() {
        Assertions.assertTrue(userDAO.registerCustomer(USERNAME, "Passw0rdOK", "Khách Thử", EMAIL, PHONE, null) > 0);
        Assertions.assertTrue(userDAO.exists("username", USERNAME));
        Assertions.assertTrue(userDAO.exists("email", EMAIL));
        Assertions.assertTrue(userDAO.exists("phone", PHONE));
        Assertions.assertEquals(UserDAO.REGISTER_DUPLICATE,
                userDAO.registerCustomer(USERNAME, "Passw0rdOK", "Khác", "other@example.com", "0999000222", null));
    }

    @Test
    @DisplayName("Tài khoản nhân viên được liên kết với employees")
    public void staffAccountsLinkedToEmployees() throws Exception {
        try (Connection c = DBContext.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT COUNT(*) FROM users u JOIN roles r ON u.role_id = r.role_id "
                   + "WHERE r.role_name <> 'CUSTOMER' AND NOT EXISTS (SELECT 1 FROM employees e WHERE e.user_id = u.user_id)");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            Assertions.assertEquals(0, rs.getInt(1), "Mọi tài khoản nhân viên phải có hồ sơ employees");
        }
    }

    @Test
    @DisplayName("Token đặt lại mật khẩu: dùng một lần")
    public void tokenIsSingleUse() {
        int id = userDAO.registerCustomer(USERNAME, "Passw0rdOK", "Khách Thử", EMAIL, PHONE, null);
        String token = resetDAO.createToken(id);
        Assertions.assertNotNull(token);
        Assertions.assertTrue(resetDAO.isTokenValid(token));

        Assertions.assertTrue(resetDAO.resetPassword(token, "NewPassw0rd"));
        Assertions.assertNotNull(userDAO.authenticate(USERNAME, "NewPassw0rd"));
        Assertions.assertNull(userDAO.authenticate(USERNAME, "Passw0rdOK"));

        Assertions.assertFalse(resetDAO.isTokenValid(token));
        Assertions.assertFalse(resetDAO.resetPassword(token, "AnotherPass1"), "Token đã dùng không được dùng lại");
        Assertions.assertNull(userDAO.authenticate(USERNAME, "AnotherPass1"));
    }

    @Test
    @DisplayName("Token hết hạn hoặc bị thay thế bởi token mới đều không dùng được")
    public void tokenExpiresAndSuperseded() throws Exception {
        int id = userDAO.registerCustomer(USERNAME, "Passw0rdOK", "Khách Thử", EMAIL, PHONE, null);
        String old = resetDAO.createToken(id);
        String fresh = resetDAO.createToken(id);
        Assertions.assertFalse(resetDAO.isTokenValid(old), "Token cũ phải bị vô hiệu khi có token mới");
        Assertions.assertTrue(resetDAO.isTokenValid(fresh));

        try (Connection c = DBContext.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE password_reset_tokens SET expires_at = DATE_SUB(NOW(), INTERVAL 1 MINUTE) WHERE user_id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
        Assertions.assertFalse(resetDAO.isTokenValid(fresh));
        Assertions.assertFalse(resetDAO.resetPassword(fresh, "NewPassw0rd"));
        Assertions.assertNotNull(userDAO.authenticate(USERNAME, "Passw0rdOK"), "Mật khẩu cũ giữ nguyên");
        Assertions.assertFalse(resetDAO.isTokenValid("khong-ton-tai"));
    }
}
