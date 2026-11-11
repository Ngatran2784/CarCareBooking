package com.carcare.dal;

import org.mindrot.jbcrypt.BCrypt;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

/**
 * Data Access Object: PasswordResetDAO
 * Quản lý token đặt lại mật khẩu: dùng một lần, có thời hạn.
 * Chỉ lưu SHA-256 của token trong CSDL; token gốc chỉ xuất hiện trong liên kết gửi cho người dùng.
 */
public class PasswordResetDAO extends DBContext {

    /** Thời hạn hiệu lực của token (phút). */
    public static final int TOKEN_TTL_MINUTES = 30;

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Tạo token mới cho người dùng, vô hiệu hóa các token chưa dùng trước đó.
     * @return token gốc (chưa băm) hoặc null nếu lỗi
     */
    public String createToken(int userId) {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement inv = conn.prepareStatement(
                    "UPDATE password_reset_tokens SET used_at = NOW() WHERE user_id = ? AND used_at IS NULL")) {
                inv.setInt(1, userId);
                inv.executeUpdate();
            }
            try (PreparedStatement ins = conn.prepareStatement(
                    "INSERT INTO password_reset_tokens (user_id, token_hash, expires_at) "
                  + "VALUES (?, ?, DATE_ADD(NOW(), INTERVAL ? MINUTE))")) {
                ins.setInt(1, userId);
                ins.setString(2, hash(rawToken));
                ins.setInt(3, TOKEN_TTL_MINUTES);
                ins.executeUpdate();
            }
            conn.commit();
            return rawToken;
        } catch (SQLException e) {
            rollback(conn);
            System.err.println("[PasswordResetDAO] Loi tao token: " + e.getMessage());
            return null;
        } finally {
            close(conn, null, null);
        }
    }

    /** Kiểm tra token còn hiệu lực (tồn tại, chưa dùng, chưa hết hạn). */
    public boolean isTokenValid(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            return false;
        }
        String sql = "SELECT 1 FROM password_reset_tokens "
                   + "WHERE token_hash = ? AND used_at IS NULL AND expires_at > NOW()";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, hash(rawToken));
            rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("[PasswordResetDAO] Loi kiem tra token: " + e.getMessage());
            return false;
        } finally {
            close(conn, stmt, rs);
        }
    }

    /**
     * Đặt mật khẩu mới bằng token. Token được đánh dấu đã dùng và mật khẩu được đổi
     * trong cùng một giao dịch; hai yêu cầu đồng thời chỉ một yêu cầu thành công.
     * @return true nếu đổi mật khẩu thành công
     */
    public boolean resetPassword(String rawToken, String newRawPassword) {
        if (rawToken == null || rawToken.isBlank() || newRawPassword == null || newRawPassword.isEmpty()) {
            return false;
        }
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            int userId = -1;
            // Khóa dòng token để chống dùng lại đồng thời
            try (PreparedStatement sel = conn.prepareStatement(
                    "SELECT user_id FROM password_reset_tokens "
                  + "WHERE token_hash = ? AND used_at IS NULL AND expires_at > NOW() FOR UPDATE")) {
                sel.setString(1, hash(rawToken));
                try (ResultSet rs = sel.executeQuery()) {
                    if (rs.next()) userId = rs.getInt("user_id");
                }
            }
            if (userId < 0) {
                conn.rollback();
                return false;
            }

            try (PreparedStatement upd = conn.prepareStatement(
                    "UPDATE users SET password_hash = ?, updated_at = NOW() WHERE user_id = ?")) {
                upd.setString(1, BCrypt.hashpw(newRawPassword, BCrypt.gensalt(10)));
                upd.setInt(2, userId);
                upd.executeUpdate();
            }
            // Đánh dấu đã dùng token này và vô hiệu hóa mọi token còn lại của người dùng
            try (PreparedStatement inv = conn.prepareStatement(
                    "UPDATE password_reset_tokens SET used_at = NOW() WHERE user_id = ? AND used_at IS NULL")) {
                inv.setInt(1, userId);
                inv.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            rollback(conn);
            System.err.println("[PasswordResetDAO] Loi dat lai mat khau: " + e.getMessage());
            return false;
        } finally {
            close(conn, null, null);
        }
    }

    private static String hash(String rawToken) {
        try {
            byte[] d = MessageDigest.getInstance("SHA-256").digest(rawToken.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : d) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static void rollback(Connection conn) {
        if (conn != null) {
            try { conn.rollback(); } catch (SQLException ignored) {}
        }
    }
}
