package com.carcare.dal;

import com.carcare.model.Role;
import com.carcare.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object: UserDAO
 * Quản lý truy xuất dữ liệu tài khoản và vai trò trong hệ thống MySQL.
 */
public class UserDAO extends DBContext {

    /**
     * Tìm kiếm người dùng bằng username hoặc email (dùng cho đăng nhập).
     */
    public User findByUsernameOrEmail(String identifier) {
        if (identifier == null || identifier.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT u.user_id, u.username, u.password_hash, u.full_name, u.email, u.phone, "
                   + "       u.role_id, u.status, u.created_at, u.updated_at, "
                   + "       r.role_name, r.description AS role_desc "
                   + "FROM users u "
                   + "LEFT JOIN roles r ON u.role_id = r.role_id "
                   + "WHERE u.username = ? OR u.email = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, identifier.trim());
            stmt.setString(2, identifier.trim());
            rs = stmt.executeQuery();

            if (rs.next()) {
                User user = mapResultSetToUser(rs);
                user.setRoles(findRolesByUserId(user.getUserId()));
                return user;
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Loi khi tim nguoi dung boi identifier (" + identifier + "): " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }

        return null;
    }

    /**
     * Xác thực thông tin đăng nhập:
     * Kiểm tra username/email và đối soát mật khẩu đã băm bằng BCrypt.
     * Trả về User nếu thông tin hợp lệ (bao gồm cả tài khoản LOCKED để Servlet xử lý thông báo),
     * trả về null nếu sai tên đăng nhập hoặc sai mật khẩu.
     */
    public User authenticate(String identifier, String rawPassword) {
        if (identifier == null || rawPassword == null) {
            return null;
        }

        User user = findByUsernameOrEmail(identifier);
        if (user == null || user.getPasswordHash() == null) {
            return null;
        }

        try {
            boolean passwordMatches = BCrypt.checkpw(rawPassword, user.getPasswordHash());
            if (passwordMatches) {
                return user;
            }
        } catch (Exception e) {
            System.err.println("[UserDAO] Loi xac thuc BCrypt: " + e.getMessage());
        }

        return null;
    }

    /**
     * Tìm kiếm người dùng theo ID.
     */
    public User findById(int userId) {
        String sql = "SELECT u.user_id, u.username, u.password_hash, u.full_name, u.email, u.phone, "
                   + "       u.role_id, u.status, u.created_at, u.updated_at, "
                   + "       r.role_name, r.description AS role_desc "
                   + "FROM users u "
                   + "LEFT JOIN roles r ON u.role_id = r.role_id "
                   + "WHERE u.user_id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                User user = mapResultSetToUser(rs);
                user.setRoles(findRolesByUserId(userId));
                return user;
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Loi tim nguoi dung theo ID (" + userId + "): " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }

        return null;
    }

    /**
     * Lấy danh sách toàn bộ vai trò của một tài khoản thông qua bảng liên kết user_roles.
     */
    public List<Role> findRolesByUserId(int userId) {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT r.role_id, r.role_name, r.description, r.created_at "
                   + "FROM roles r "
                   + "INNER JOIN user_roles ur ON r.role_id = ur.role_id "
                   + "WHERE ur.user_id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Role role = new Role();
                role.setRoleId(rs.getInt("role_id"));
                role.setRoleName(rs.getString("role_name"));
                role.setDescription(rs.getString("description"));
                Timestamp ts = rs.getTimestamp("created_at");
                if (ts != null) {
                    role.setCreatedAt(ts.toLocalDateTime());
                }
                roles.add(role);
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Loi lay danh sach vai tro cua user (" + userId + "): " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }

        return roles;
    }

    /**
     * Cập nhật mật khẩu mới đã băm cho người dùng.
     */
    public boolean updatePassword(int userId, String newRawPassword) {
        if (newRawPassword == null || newRawPassword.trim().isEmpty()) {
            return false;
        }

        String hashedPassword = BCrypt.hashpw(newRawPassword, BCrypt.gensalt(10));
        String sql = "UPDATE users SET password_hash = ?, updated_at = NOW() WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, hashedPassword);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] Loi cap nhat mat khau cho user (" + userId + "): " + e.getMessage());
            return false;
        } finally {
            close(conn, stmt, null);
        }
    }

    /**
     * Cập nhật trạng thái người dùng (ACTIVE, INACTIVE, LOCKED).
     */
    public boolean updateStatus(int userId, String status) {
        String sql = "UPDATE users SET status = ?, updated_at = NOW() WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] Loi cap nhat trang thai user (" + userId + "): " + e.getMessage());
            return false;
        } finally {
            close(conn, stmt, null);
        }
    }

    /**
     * Lấy danh sách toàn bộ người dùng hệ thống.
     */
    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.user_id, u.username, u.password_hash, u.full_name, u.email, u.phone, "
                   + "       u.role_id, u.status, u.created_at, u.updated_at, "
                   + "       r.role_name, r.description AS role_desc "
                   + "FROM users u "
                   + "LEFT JOIN roles r ON u.role_id = r.role_id "
                   + "ORDER BY u.user_id ASC";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                User user = mapResultSetToUser(rs);
                list.add(user);
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Loi lay danh sach user: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }

        return list;
    }

    /** Kiểm tra giá trị đã tồn tại trong users ở cột cho trước (username, email, phone). */
    public boolean exists(String column, String value) {
        if (!("username".equals(column) || "email".equals(column) || "phone".equals(column))) {
            throw new IllegalArgumentException("Cot khong hop le: " + column);
        }
        String sql = "SELECT 1 FROM users WHERE " + column + " = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, value);
            rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("[UserDAO] Loi kiem tra ton tai " + column + ": " + e.getMessage());
            return true; // an toàn: coi như đã tồn tại để không tạo trùng khi lỗi
        } finally {
            close(conn, stmt, rs);
        }
    }

    /** Mã trả về của registerCustomer khi username/email/phone bị trùng. */
    public static final int REGISTER_DUPLICATE = -1;
    /** Mã trả về của registerCustomer khi gặp lỗi khác. */
    public static final int REGISTER_ERROR = 0;

    /**
     * Đăng ký tài khoản khách hàng trong một giao dịch duy nhất:
     * users (vai trò CUSTOMER) + user_roles + customers (liên kết 1-1 qua customers.user_id).
     * Nếu số điện thoại đã có hồ sơ khách hàng do lễ tân tạo trước đó (chưa gắn tài khoản)
     * và cùng email thì hồ sơ đó được liên kết với tài khoản mới thay vì tạo bản sao.
     *
     * @return user_id mới (&gt; 0), {@link #REGISTER_DUPLICATE} hoặc {@link #REGISTER_ERROR}
     */
    public int registerCustomer(String username, String rawPassword, String fullName,
                                String email, String phone, String address) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            int roleId;
            try (PreparedStatement ps = conn.prepareStatement("SELECT role_id FROM roles WHERE role_name = 'CUSTOMER'");
                 ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    conn.rollback();
                    return REGISTER_ERROR;
                }
                roleId = rs.getInt(1);
            }

            int userId;
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO users (username, password_hash, full_name, email, phone, role_id, status) "
                  + "VALUES (?, ?, ?, ?, ?, ?, 'ACTIVE')", java.sql.Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, username);
                ps.setString(2, BCrypt.hashpw(rawPassword, BCrypt.gensalt(10)));
                ps.setString(3, fullName);
                ps.setString(4, email);
                ps.setString(5, phone);
                ps.setInt(6, roleId);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) {
                        conn.rollback();
                        return REGISTER_ERROR;
                    }
                    userId = keys.getInt(1);
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)")) {
                ps.setInt(1, userId);
                ps.setInt(2, roleId);
                ps.executeUpdate();
            }

            // Liên kết với hồ sơ khách hàng có sẵn (cùng SĐT và email, chưa gắn tài khoản) hoặc tạo mới
            int linked;
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE customers SET user_id = ?, full_name = ? "
                  + "WHERE phone = ? AND user_id IS NULL AND LOWER(email) = LOWER(?)")) {
                ps.setInt(1, userId);
                ps.setString(2, fullName);
                ps.setString(3, phone);
                ps.setString(4, email);
                linked = ps.executeUpdate();
            }
            if (linked == 0) {
                int customerId;
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO customers (user_id, full_name, phone, email, address) VALUES (?, ?, ?, ?, ?)",
                        java.sql.Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, userId);
                    ps.setString(2, fullName);
                    ps.setString(3, phone);
                    ps.setString(4, email);
                    ps.setString(5, address);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        customerId = keys.getInt(1);
                    }
                }
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE customers SET customer_code = ? WHERE customer_id = ?")) {
                    ps.setString(1, String.format("CUST-%04d", customerId));
                    ps.setInt(2, customerId);
                    ps.executeUpdate();
                }
            }

            conn.commit();
            return userId;
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            rollbackQuietly(conn);
            return REGISTER_DUPLICATE;
        } catch (SQLException e) {
            rollbackQuietly(conn);
            System.err.println("[UserDAO] Loi dang ky khach hang: " + e.getMessage());
            return REGISTER_ERROR;
        } finally {
            close(conn, null, null);
        }
    }

    private static void rollbackQuietly(Connection conn) {
        if (conn != null) {
            try { conn.rollback(); } catch (SQLException ignored) {}
        }
    }

    /** Tìm tài khoản ACTIVE theo email (dùng cho quên mật khẩu). */
    public User findActiveByEmail(String email) {
        User user = findByUsernameOrEmail(email);
        if (user != null && user.getEmail() != null
                && user.getEmail().equalsIgnoreCase(email.trim()) && user.isActive() && !user.isLocked()) {
            return user;
        }
        return null;
    }

    /**
     * Ánh xạ từ dòng ResultSet sang đối tượng User.
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setRoleId(rs.getInt("role_id"));
        user.setStatus(rs.getString("status"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) user.setCreatedAt(createdAt.toLocalDateTime());

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) user.setUpdatedAt(updatedAt.toLocalDateTime());

        String roleName = rs.getString("role_name");
        if (roleName != null) {
            Role role = new Role(user.getRoleId(), roleName, rs.getString("role_desc"));
            user.setRole(role);
        }

        return user;
    }
}
