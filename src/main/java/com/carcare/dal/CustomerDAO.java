package com.carcare.dal;

import com.carcare.model.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object: CustomerDAO
 * Quản lý truy xuất dữ liệu hồ sơ khách hàng trong MySQL (Bảng `customers`).
 */
public class CustomerDAO extends DBContext {

    /**
     * Tìm khách hàng theo ID
     */
    public Customer findById(int customerId) {
        String sql = "SELECT c.*, (SELECT COUNT(*) FROM vehicles v WHERE v.customer_id = c.customer_id) AS vehicle_count "
                   + "FROM customers c WHERE c.customer_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, customerId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToCustomer(rs);
            }
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] Loi tim theo customerId (" + customerId + "): " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return null;
    }

    /**
     * Tìm khách hàng theo User ID (dùng cho phiên đăng nhập của Customer)
     */
    public Customer findByUserId(int userId) {
        String sql = "SELECT c.*, (SELECT COUNT(*) FROM vehicles v WHERE v.customer_id = c.customer_id) AS vehicle_count "
                   + "FROM customers c WHERE c.user_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToCustomer(rs);
            }
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] Loi tim theo userId (" + userId + "): " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return null;
    }

    /**
     * Tìm khách hàng theo số điện thoại
     */
    public Customer findByPhone(String phone) {
        if (phone == null || phone.isBlank()) return null;
        String sql = "SELECT c.*, (SELECT COUNT(*) FROM vehicles v WHERE v.customer_id = c.customer_id) AS vehicle_count "
                   + "FROM customers c WHERE c.phone = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, phone.trim());
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToCustomer(rs);
            }
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] Loi tim theo phone (" + phone + "): " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return null;
    }

    /**
     * Lấy toàn bộ danh sách khách hàng (kèm số lượng xe)
     */
    public List<Customer> findAll() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT c.*, (SELECT COUNT(*) FROM vehicles v WHERE v.customer_id = c.customer_id) AS vehicle_count "
                   + "FROM customers c ORDER BY c.customer_id DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToCustomer(rs));
            }
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] Loi lay tat ca khach hang: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return list;
    }

    /**
     * Tìm kiếm khách hàng theo từ khóa (họ tên, SĐT, email, mã KH)
     */
    public List<Customer> search(String keyword) {
        List<Customer> list = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        String sql = "SELECT c.*, (SELECT COUNT(*) FROM vehicles v WHERE v.customer_id = c.customer_id) AS vehicle_count "
                   + "FROM customers c "
                   + "WHERE c.full_name LIKE ? OR c.phone LIKE ? OR c.email LIKE ? OR c.customer_code LIKE ? "
                   + "ORDER BY c.customer_id DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            String term = "%" + keyword.trim() + "%";
            stmt.setString(1, term);
            stmt.setString(2, term);
            stmt.setString(3, term);
            stmt.setString(4, term);
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToCustomer(rs));
            }
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] Loi tim kiem khach hang: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return list;
    }

    /**
     * Thêm mới một khách hàng
     * @return ID của khách hàng mới tạo (> 0) hoặc -1 nếu lỗi
     */
    public int insert(Customer item) {
        if (item == null) return -1;
        String sql = "INSERT INTO customers (user_id, customer_code, full_name, phone, email, address, membership_level) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            if (item.getUserId() != null && item.getUserId() > 0) {
                stmt.setInt(1, item.getUserId());
            } else {
                stmt.setNull(1, java.sql.Types.INTEGER);
            }
            stmt.setString(2, item.getCustomerCode());
            stmt.setString(3, item.getFullName());
            stmt.setString(4, item.getPhone());
            stmt.setString(5, item.getEmail());
            stmt.setString(6, item.getAddress());
            stmt.setString(7, item.getMembershipLevel() != null ? item.getMembershipLevel() : "STANDARD");

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int genId = rs.getInt(1);
                    item.setCustomerId(genId);
                    // Nếu chưa có customer_code, tự sinh mã CUST-XXXX
                    if (item.getCustomerCode() == null || item.getCustomerCode().isBlank()) {
                        String generatedCode = String.format("CUST-%04d", genId);
                        item.setCustomerCode(generatedCode);
                        try (PreparedStatement updateCode = conn.prepareStatement(
                                "UPDATE customers SET customer_code = ? WHERE customer_id = ?")) {
                            updateCode.setString(1, generatedCode);
                            updateCode.setInt(2, genId);
                            updateCode.executeUpdate();
                        }
                    }
                    return genId;
                }
            }
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] Loi them khach hang: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return -1;
    }

    /**
     * Cập nhật thông tin khách hàng (Dành cho Quản trị viên / Lễ tân)
     */
    public boolean update(Customer item) {
        if (item == null || item.getCustomerId() <= 0) return false;
        String sql = "UPDATE customers SET full_name = ?, phone = ?, email = ?, address = ?, membership_level = ? "
                   + "WHERE customer_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, item.getFullName());
            stmt.setString(2, item.getPhone());
            stmt.setString(3, item.getEmail());
            stmt.setString(4, item.getAddress());
            stmt.setString(5, item.getMembershipLevel());
            stmt.setInt(6, item.getCustomerId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] Loi cap nhat khach hang: " + e.getMessage());
            return false;
        } finally {
            close(conn, stmt, null);
        }
    }

    /**
     * Cập nhật thông tin hồ sơ của chính khách hàng (không đổi hạng thành viên)
     */
    public boolean updateProfile(int customerId, String fullName, String phone, String email, String address) {
        String sql = "UPDATE customers SET full_name = ?, phone = ?, email = ?, address = ? WHERE customer_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, fullName);
            stmt.setString(2, phone);
            stmt.setString(3, email);
            stmt.setString(4, address);
            stmt.setInt(5, customerId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] Loi updateProfile: " + e.getMessage());
            return false;
        } finally {
            close(conn, stmt, null);
        }
    }

    /**
     * Xóa khách hàng
     */
    public boolean delete(int customerId) {
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, customerId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] Loi xoa khach hang: " + e.getMessage());
            return false;
        } finally {
            close(conn, stmt, null);
        }
    }

    /**
     * Kiểm tra số điện thoại đã tồn tại cho khách hàng khác chưa
     */
    public boolean existsByPhone(String phone, Integer excludeCustomerId) {
        if (phone == null || phone.isBlank()) return false;
        String sql = "SELECT 1 FROM customers WHERE phone = ?"
                   + (excludeCustomerId != null ? " AND customer_id <> ?" : "");
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, phone.trim());
            if (excludeCustomerId != null) {
                stmt.setInt(2, excludeCustomerId);
            }
            rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] Loi existsByPhone: " + e.getMessage());
            return false;
        } finally {
            close(conn, stmt, rs);
        }
    }

    /**
     * Ánh xạ ResultSet sang Entity Customer
     */
    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setCustomerId(rs.getInt("customer_id"));

        int uId = rs.getInt("user_id");
        if (!rs.wasNull()) {
            c.setUserId(uId);
        }

        c.setCustomerCode(rs.getString("customer_code"));
        c.setFullName(rs.getString("full_name"));
        c.setPhone(rs.getString("phone"));
        c.setEmail(rs.getString("email"));
        c.setAddress(rs.getString("address"));
        c.setMembershipLevel(rs.getString("membership_level"));

        Timestamp tsCreated = rs.getTimestamp("created_at");
        if (tsCreated != null) {
            c.setCreatedAt(tsCreated.toLocalDateTime());
        }
        Timestamp tsUpdated = rs.getTimestamp("updated_at");
        if (tsUpdated != null) {
            c.setUpdatedAt(tsUpdated.toLocalDateTime());
        }

        // Đọc trường đếm số xe nếu có trong ResultSet
        try {
            c.setVehicleCount(rs.getInt("vehicle_count"));
        } catch (SQLException ignored) {}

        return c;
    }
}
