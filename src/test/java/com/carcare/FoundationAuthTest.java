package com.carcare;

import com.carcare.dal.DBContext;
import com.carcare.dal.ReportDAO;
import com.carcare.dal.UserDAO;
import com.carcare.model.Role;
import com.carcare.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Kiểm thử tự động các chức năng nền tảng:
 * 1. Kết nối CSDL MySQL (carcare_db)
 * 2. Xác thực đăng nhập thành công
 * 3. Từ chối đăng nhập khi sai mật khẩu hoặc tài khoản không tồn tại
 * 4. Phát hiện tài khoản bị khóa (LOCKED)
 * 5. Băm mật khẩu an toàn với BCrypt
 * 6. Truy xuất danh sách vai trò từ user_roles
 * 7. Kiểm tra phân quyền RBAC trên Model User
 * 8. Số liệu Dashboard từ ReportDAO
 */
public class FoundationAuthTest {

    private UserDAO userDAO;
    private ReportDAO reportDAO;

    @BeforeEach
    public void setUp() {
        userDAO = new UserDAO();
        reportDAO = new ReportDAO();
    }

    @Test
    @DisplayName("TC-01: Kiểm tra kết nối tới cơ sở dữ liệu MySQL carcare_db")
    public void testMySQLConnection() throws SQLException {
        try (Connection conn = DBContext.getConnection()) {
            Assertions.assertNotNull(conn, "Ket noi MySQL khong duoc null");
            Assertions.assertFalse(conn.isClosed(), "Ket noi MySQL phai o trang thai mo");
        }
    }

    @Test
    @DisplayName("TC-02: Đăng nhập thành công với tài khoản và mật khẩu hợp lệ (admin)")
    public void testAuthenticateSuccess() {
        User user = userDAO.authenticate("admin", "Admin@123");
        Assertions.assertNotNull(user, "Dang nhap admin phai thanh cong");
        Assertions.assertEquals("admin", user.getUsername());
        Assertions.assertEquals("ACTIVE", user.getStatus());
        Assertions.assertTrue(user.isActive());
        Assertions.assertFalse(user.isLocked());
        Assertions.assertTrue(user.hasRole("ADMIN"), "User admin phai co vai tro ADMIN");
    }

    @Test
    @DisplayName("TC-03: Đăng nhập bằng địa chỉ email hợp lệ")
    public void testAuthenticateByEmail() {
        User user = userDAO.authenticate("manager@carcare.vn", "Manager@123");
        Assertions.assertNotNull(user, "Dang nhap bang email phai thanh cong");
        Assertions.assertEquals("manager", user.getUsername());
        Assertions.assertTrue(user.hasRole("MANAGER"), "User manager phai co vai tro MANAGER");
    }

    @Test
    @DisplayName("TC-04: Sai mật khẩu bị từ chối đăng nhập")
    public void testAuthenticateWrongPassword() {
        User user = userDAO.authenticate("admin", "MatKhauSai@999");
        Assertions.assertNull(user, "Dang nhap voi mat khau sai phai tra ve null");
    }

    @Test
    @DisplayName("TC-05: Tài khoản không tồn tại bị từ chối đăng nhập")
    public void testAuthenticateNonExistentUser() {
        User user = userDAO.authenticate("nguoidung_khong_ton_tai", "MatKhau@123");
        Assertions.assertNull(user, "Tai khoan khong ton tai phai tra ve null");
    }

    @Test
    @DisplayName("TC-06: Tài khoản bị khóa (LOCKED) được nhận diện chính xác")
    public void testAuthenticateLockedUser() {
        User user = userDAO.authenticate("locked_user", "Locked@123");
        Assertions.assertNotNull(user, "Tai khoan locked_user ton tai va dung mat khau");
        Assertions.assertTrue(user.isLocked(), "Tai khoan phai o trang thai LOCKED");
        Assertions.assertFalse(user.isActive(), "Tai khoan LOCKED khong duoc active");
    }

    @Test
    @DisplayName("TC-07: Truy xuất vai trò từ bảng user_roles")
    public void testFindRolesByUserId() {
        List<Role> roles = userDAO.findRolesByUserId(1); // admin ID
        Assertions.assertNotNull(roles, "Danh sach vai tro khong duoc null");
        Assertions.assertFalse(roles.isEmpty(), "User admin phai co it nhat 1 vai tro");
        boolean hasAdmin = roles.stream().anyMatch(r -> "ADMIN".equalsIgnoreCase(r.getRoleName()));
        Assertions.assertTrue(hasAdmin, "Danh sach vai tro phai chua ADMIN");
    }

    @Test
    @DisplayName("TC-08: Kiểm tra độ an toàn của thuật toán băm BCrypt")
    public void testBCryptSecurity() {
        String plainPassword = "TestPassword@2026";
        String hash1 = BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));
        String hash2 = BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));

        // Hai lan bam cua cung mot mat khau phai cho ra hai hash khac nhau nho salt ngau nhien
        Assertions.assertNotEquals(hash1, hash2, "BCrypt salt ngau nhien giup chong Rainbow table");
        Assertions.assertTrue(BCrypt.checkpw(plainPassword, hash1));
        Assertions.assertTrue(BCrypt.checkpw(plainPassword, hash2));
    }

    @Test
    @DisplayName("TC-09: Kiểm tra phương thức hasRole trên User Model")
    public void testUserRoleChecking() {
        User tech = userDAO.authenticate("technician", "Tech@123");
        Assertions.assertNotNull(tech);
        Assertions.assertTrue(tech.hasRole("TECHNICIAN"));
        Assertions.assertFalse(tech.hasRole("ADMIN"), "Technician khong the co quyen ADMIN");
    }

    @Test
    @DisplayName("TC-10: Kiểm tra số liệu thống kê Dashboard từ ReportDAO không phát sinh lỗi")
    public void testDashboardStatistics() {
        Map<String, Object> stats = reportDAO.getDashboardStatistics();
        Assertions.assertNotNull(stats, "Thong ke Dashboard khong duoc null");
        Assertions.assertTrue(stats.containsKey("totalBookings"));
        Assertions.assertTrue(stats.containsKey("todayBookings"));
        Assertions.assertTrue(stats.containsKey("inProgressVehicles"));
        Assertions.assertTrue(stats.containsKey("activeJobs"));
        Assertions.assertTrue(stats.containsKey("occupiedBays"));
        Assertions.assertTrue(stats.containsKey("totalBays"));
    }
}
