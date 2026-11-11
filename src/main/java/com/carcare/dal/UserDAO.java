package com.carcare.dal;

import com.carcare.model.Role;
import com.carcare.model.User;
import com.carcare.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserDAO {
    private static final String USER_COLUMNS =
            "u.user_id, u.username, u.full_name, u.email, u.phone, "
                    + "u.role_id, u.status, u.created_at, u.updated_at, "
                    + "r.role_id AS joined_role_id, r.role_name, r.description AS role_desc, "
                    + "r.created_at AS role_created_at";
    private static final String LOGIN_COLUMNS = USER_COLUMNS + ", u.password_hash";

    public User findByUsernameOrEmail(String identifier) throws SQLException {
        if (identifier == null || identifier.isBlank()) {
            return null;
        }

        String sql = "SELECT " + USER_COLUMNS + " FROM users u "
                + "LEFT JOIN roles r ON u.role_id = r.role_id "
                + "WHERE u.username = ? OR u.email = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, identifier.trim());
            ps.setString(2, identifier.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapResultSetToUser(rs, false) : null;
            }
        }
    }

    public User findByUsername(String username) throws SQLException {
        if (username == null || username.isBlank()) {
            return null;
        }
        return findByUniqueValue("u.username", username.trim());
    }

    public User findByEmail(String email) throws SQLException {
        if (email == null || email.isBlank()) {
            return null;
        }
        return findByUniqueValue("u.email", email.trim());
    }

    private User findByUniqueValue(String column, String value) throws SQLException {
        String sql = "SELECT " + USER_COLUMNS + " FROM users u "
                + "LEFT JOIN roles r ON u.role_id = r.role_id WHERE " + column + " = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapResultSetToUser(rs, false) : null;
            }
        }
    }

    public User login(String usernameOrEmail, String plainPassword) throws SQLException {
        if (usernameOrEmail == null || usernameOrEmail.isBlank() || plainPassword == null) {
            return null;
        }

        String sql = "SELECT " + LOGIN_COLUMNS + " FROM users u "
                + "LEFT JOIN roles r ON u.role_id = r.role_id "
                + "WHERE (u.username = ? OR u.email = ?) AND u.status = 'ACTIVE'";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String identifier = usernameOrEmail.trim();
            ps.setString(1, identifier);
            ps.setString(2, identifier);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                User user = mapResultSetToUser(rs, true);
                if (!PasswordUtil.verifyPassword(plainPassword, user.getPasswordHash())) {
                    return null;
                }
                user.setPasswordHash(null);
                return user;
            }
        }
    }

    public User findById(int userId) throws SQLException {
        String sql = "SELECT " + USER_COLUMNS + " FROM users u "
                + "LEFT JOIN roles r ON u.role_id = r.role_id WHERE u.user_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapResultSetToUser(rs, false) : null;
            }
        }
    }

    public List<User> findAll() throws SQLException {
        String sql = "SELECT " + USER_COLUMNS + " FROM users u "
                + "LEFT JOIN roles r ON u.role_id = r.role_id ORDER BY u.user_id";
        return executeUserList(sql, null);
    }

    public List<User> search(String searchTerm) throws SQLException {
        if (searchTerm == null || searchTerm.isBlank()) {
            return findAll();
        }

        String sql = "SELECT " + USER_COLUMNS + " FROM users u "
                + "LEFT JOIN roles r ON u.role_id = r.role_id "
                + "WHERE u.username LIKE ? OR u.full_name LIKE ? OR u.email LIKE ? OR u.phone LIKE ? "
                + "ORDER BY u.user_id";
        String pattern = "%" + searchTerm.trim() + "%";
        return executeUserList(sql, pattern);
    }

    private List<User> executeUserList(String sql, String searchPattern) throws SQLException {
        List<User> users = new ArrayList<>();
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (searchPattern != null) {
                for (int parameterIndex = 1; parameterIndex <= 4; parameterIndex++) {
                    ps.setString(parameterIndex, searchPattern);
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs, false));
                }
            }
        }
        return users;
    }

    public boolean existsByUsernameOrEmail(String identifier) throws SQLException {
        if (identifier == null || identifier.isBlank()) {
            return false;
        }
        return exists("SELECT 1 FROM users WHERE username = ? OR email = ? LIMIT 1",
                identifier.trim(), identifier.trim());
    }

    public boolean existsByUsername(String username) throws SQLException {
        return existsForValue("username", username);
    }

    public boolean existsByEmail(String email) throws SQLException {
        return existsForValue("email", email);
    }

    public boolean existsByPhone(String phone) throws SQLException {
        return existsForValue("phone", phone);
    }

    private boolean existsForValue(String column, String value) throws SQLException {
        if (value == null || value.isBlank()) {
            return false;
        }
        return exists("SELECT 1 FROM users WHERE " + column + " = ? LIMIT 1", value.trim());
    }

    private boolean exists(String sql, String... values) throws SQLException {
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                ps.setString(i + 1, values[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean insert(User user) throws SQLException {
        if (user == null) {
            throw new IllegalArgumentException("User must not be null");
        }
        String plainPassword = user.getPasswordHash();
        user.setPasswordHash(null);
        return insert(user, plainPassword);
    }

    public boolean insert(User user, String plainPassword) throws SQLException {
        if (user == null) {
            throw new IllegalArgumentException("User must not be null");
        }
        String passwordHash = PasswordUtil.hashPassword(plainPassword);
        if (passwordHash == null) {
            throw new IllegalArgumentException("Password must not be blank");
        }

        String sql = "INSERT INTO users "
                + "(username, password_hash, full_name, email, phone, role_id, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, passwordHash);
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPhone());
            ps.setInt(6, user.getRoleId());
            ps.setString(7, user.getStatus() == null ? "ACTIVE" : user.getStatus().toUpperCase(Locale.ROOT));
            return ps.executeUpdate() == 1;
        }
    }

    public boolean update(User user) throws SQLException {
        if (user == null) {
            throw new IllegalArgumentException("User must not be null");
        }
        String sql = "UPDATE users SET username = ?, full_name = ?, email = ?, phone = ?, "
                + "role_id = ?, status = ? WHERE user_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPhone());
            ps.setInt(5, user.getRoleId());
            ps.setString(6, user.getStatus() == null ? "ACTIVE" : user.getStatus().toUpperCase(Locale.ROOT));
            ps.setInt(7, user.getUserId());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean updatePassword(int userId, String plainPassword) throws SQLException {
        String passwordHash = PasswordUtil.hashPassword(plainPassword);
        if (passwordHash == null) {
            throw new IllegalArgumentException("Password must not be blank");
        }
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, passwordHash);
            ps.setInt(2, userId);
            return ps.executeUpdate() == 1;
        }
    }

    private User mapResultSetToUser(ResultSet rs, boolean includePasswordHash) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        if (includePasswordHash) {
            user.setPasswordHash(rs.getString("password_hash"));
        }
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setRoleId(rs.getInt("role_id"));
        user.setStatus(rs.getString("status"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            user.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        int joinedRoleId = rs.getInt("joined_role_id");
        if (!rs.wasNull()) {
            Role role = new Role();
            role.setRoleId(joinedRoleId);
            role.setRoleName(rs.getString("role_name"));
            role.setDescription(rs.getString("role_desc"));
            Timestamp roleCreatedAt = rs.getTimestamp("role_created_at");
            if (roleCreatedAt != null) {
                role.setCreatedAt(roleCreatedAt.toLocalDateTime());
            }
            user.setRole(role);
        }
        return user;
    }
}
