package com.carcare.dal;

import com.carcare.model.Vehicle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object: VehicleDAO
 * Quản lý truy xuất dữ liệu phương tiện xe trong MySQL (Bảng `vehicles`).
 */
public class VehicleDAO extends DBContext {

    private static final String SELECT_BASE =
            "SELECT v.*, c.full_name AS customer_name, c.phone AS customer_phone, c.customer_code "
          + "FROM vehicles v "
          + "JOIN customers c ON v.customer_id = c.customer_id ";

    /**
     * Tìm phương tiện theo ID
     */
    public Vehicle findById(int vehicleId) {
        String sql = SELECT_BASE + "WHERE v.vehicle_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, vehicleId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToVehicle(rs);
            }
        } catch (SQLException e) {
            System.err.println("[VehicleDAO] Loi tim theo vehicleId (" + vehicleId + "): " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return null;
    }

    /**
     * Tìm phương tiện theo biển số xe
     */
    public Vehicle findByLicensePlate(String licensePlate) {
        if (licensePlate == null || licensePlate.isBlank()) return null;
        String sql = SELECT_BASE + "WHERE UPPER(REPLACE(v.license_plate, ' ', '')) = UPPER(REPLACE(?, ' ', ''))";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, licensePlate.trim());
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToVehicle(rs);
            }
        } catch (SQLException e) {
            System.err.println("[VehicleDAO] Loi tim theo licensePlate (" + licensePlate + "): " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return null;
    }

    /**
     * Lấy danh sách phương tiện của một khách hàng cụ thể (dùng cho Customer view và màn hình đặt lịch S2-07)
     */
    public List<Vehicle> findByCustomerId(int customerId) {
        List<Vehicle> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE v.customer_id = ? ORDER BY v.vehicle_id DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, customerId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToVehicle(rs));
            }
        } catch (SQLException e) {
            System.err.println("[VehicleDAO] Loi lay xe theo customerId (" + customerId + "): " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return list;
    }

    /**
     * Lấy toàn bộ danh sách phương tiện trong hệ thống (dành cho Admin / Lễ tân)
     */
    public List<Vehicle> findAll() {
        List<Vehicle> list = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY v.vehicle_id DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToVehicle(rs));
            }
        } catch (SQLException e) {
            System.err.println("[VehicleDAO] Loi lay tat ca xe: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return list;
    }

    /**
     * Tìm kiếm phương tiện theo từ khóa (biển số, hãng, model, tên khách hàng, SĐT)
     */
    public List<Vehicle> search(String keyword) {
        List<Vehicle> list = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        String sql = SELECT_BASE
                   + "WHERE v.license_plate LIKE ? OR v.brand LIKE ? OR v.model LIKE ? "
                   + "   OR c.full_name LIKE ? OR c.phone LIKE ? "
                   + "ORDER BY v.vehicle_id DESC";
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
            stmt.setString(5, term);
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToVehicle(rs));
            }
        } catch (SQLException e) {
            System.err.println("[VehicleDAO] Loi tim kiem xe: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return list;
    }

    /**
     * Thêm mới một phương tiện
     * @return ID của xe mới tạo (> 0) hoặc -1 nếu lỗi
     */
    public int insert(Vehicle vehicle) {
        if (vehicle == null) return -1;
        String sql = "INSERT INTO vehicles (customer_id, license_plate, brand, model, year, color, vehicle_type) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, vehicle.getCustomerId());
            stmt.setString(2, vehicle.getLicensePlate() != null ? vehicle.getLicensePlate().trim().toUpperCase() : "");
            stmt.setString(3, vehicle.getBrand());
            stmt.setString(4, vehicle.getModel());

            if (vehicle.getYear() != null && vehicle.getYear() > 0) {
                stmt.setInt(5, vehicle.getYear());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }

            stmt.setString(6, vehicle.getColor());
            stmt.setString(7, vehicle.getVehicleType() != null ? vehicle.getVehicleType().toUpperCase() : "SEDAN");

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int genId = rs.getInt(1);
                    vehicle.setVehicleId(genId);
                    return genId;
                }
            }
        } catch (SQLException e) {
            System.err.println("[VehicleDAO] Loi them xe: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return -1;
    }

    /**
     * Cập nhật thông tin phương tiện
     */
    public boolean update(Vehicle vehicle) {
        if (vehicle == null || vehicle.getVehicleId() <= 0) return false;
        String sql = "UPDATE vehicles SET license_plate = ?, brand = ?, model = ?, year = ?, color = ?, vehicle_type = ? "
                   + "WHERE vehicle_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, vehicle.getLicensePlate() != null ? vehicle.getLicensePlate().trim().toUpperCase() : "");
            stmt.setString(2, vehicle.getBrand());
            stmt.setString(3, vehicle.getModel());

            if (vehicle.getYear() != null && vehicle.getYear() > 0) {
                stmt.setInt(4, vehicle.getYear());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }

            stmt.setString(5, vehicle.getColor());
            stmt.setString(6, vehicle.getVehicleType() != null ? vehicle.getVehicleType().toUpperCase() : "SEDAN");
            stmt.setInt(7, vehicle.getVehicleId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[VehicleDAO] Loi cap nhat xe: " + e.getMessage());
            return false;
        } finally {
            close(conn, stmt, null);
        }
    }

    /**
     * Xóa phương tiện
     */
    public boolean delete(int vehicleId) {
        String sql = "DELETE FROM vehicles WHERE vehicle_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, vehicleId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[VehicleDAO] Loi xoa xe (" + vehicleId + "): " + e.getMessage());
            return false;
        } finally {
            close(conn, stmt, null);
        }
    }

    /**
     * Kiểm tra quyền sở hữu xe phía máy chủ (Server-side ownership verification)
     * Đảm bảo khách hàng chỉ được xem, sửa, xóa xe thuộc hồ sơ của chính mình!
     */
    public boolean isOwner(int vehicleId, int customerId) {
        String sql = "SELECT 1 FROM vehicles WHERE vehicle_id = ? AND customer_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, vehicleId);
            stmt.setInt(2, customerId);
            rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("[VehicleDAO] Loi kiem tra quyen so huu xe: " + e.getMessage());
            return false;
        } finally {
            close(conn, stmt, rs);
        }
    }

    /**
     * Kiểm tra biển số xe đã tồn tại trong hệ thống chưa (trừ xe hiện tại nếu đang sửa)
     */
    public boolean existsByLicensePlate(String licensePlate, Integer excludeVehicleId) {
        if (licensePlate == null || licensePlate.isBlank()) return false;
        String sql = "SELECT 1 FROM vehicles "
                   + "WHERE UPPER(REPLACE(license_plate, ' ', '')) = UPPER(REPLACE(?, ' ', '')) "
                   + (excludeVehicleId != null ? " AND vehicle_id <> ?" : "");
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, licensePlate.trim());
            if (excludeVehicleId != null) {
                stmt.setInt(2, excludeVehicleId);
            }
            rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("[VehicleDAO] Loi existsByLicensePlate: " + e.getMessage());
            return false;
        } finally {
            close(conn, stmt, rs);
        }
    }

    /**
     * Đếm số lượng xe của một khách hàng
     */
    public int countByCustomerId(int customerId) {
        String sql = "SELECT COUNT(*) FROM vehicles WHERE customer_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, customerId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[VehicleDAO] Loi countByCustomerId: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return 0;
    }

    /**
     * Ánh xạ ResultSet sang Entity Vehicle
     */
    private Vehicle mapResultSetToVehicle(ResultSet rs) throws SQLException {
        Vehicle v = new Vehicle();
        v.setVehicleId(rs.getInt("vehicle_id"));
        v.setCustomerId(rs.getInt("customer_id"));
        v.setLicensePlate(rs.getString("license_plate"));
        v.setBrand(rs.getString("brand"));
        v.setModel(rs.getString("model"));

        int year = rs.getInt("year");
        if (!rs.wasNull()) {
            v.setYear(year);
        }

        v.setColor(rs.getString("color"));
        v.setVehicleType(rs.getString("vehicle_type"));

        Timestamp tsCreated = rs.getTimestamp("created_at");
        if (tsCreated != null) {
            v.setCreatedAt(tsCreated.toLocalDateTime());
        }

        // Thông tin phụ trợ từ JOIN customers (nếu có)
        try {
            v.setCustomerName(rs.getString("customer_name"));
            v.setCustomerPhone(rs.getString("customer_phone"));
            v.setCustomerCode(rs.getString("customer_code"));
        } catch (SQLException ignored) {}

        return v;
    }
}
