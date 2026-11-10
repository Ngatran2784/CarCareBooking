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
