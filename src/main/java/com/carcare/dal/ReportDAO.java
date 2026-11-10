package com.carcare.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object: ReportDAO
 * Truy vấn số liệu thống kê vận hành thực tế cho Bảng điều khiển (Dashboard).
 */
public class ReportDAO extends DBContext {

    /**
     * Lấy các chỉ số thống kê vận hành thời gian thực từ cơ sở dữ liệu.
     * Nếu chưa có dữ liệu, các giá trị trả về đúng số thực tế là 0.
     */
    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBookings", 0);
        stats.put("todayBookings", 0);
        stats.put("inProgressVehicles", 0);
        stats.put("activeJobs", 0);
        stats.put("occupiedBays", 0);
        stats.put("totalBays", 0);

        Connection conn = null;

        try {
            conn = getConnection();
            if (conn == null) {
                return stats;
            }

            // 1. Tổng số lịch hẹn
            stats.put("totalBookings", countQuery(conn, "SELECT COUNT(*) FROM bookings"));

            // 2. Lịch hẹn hôm nay
            stats.put("todayBookings", countQuery(conn, "SELECT COUNT(*) FROM bookings WHERE DATE(booking_time) = CURDATE()"));

            // 3. Xe đang chăm sóc
            stats.put("inProgressVehicles", countQuery(conn, "SELECT COUNT(*) FROM bookings WHERE status = 'IN_PROGRESS'"));

            // 4. Công việc đang thi công
            stats.put("activeJobs", countQuery(conn, "SELECT COUNT(*) FROM job_cards WHERE status IN ('IN_SERVICE', 'INSPECTING')"));

            // 5. Khoang dịch vụ đang sử dụng
            stats.put("occupiedBays", countQuery(conn, "SELECT COUNT(*) FROM service_bays WHERE status = 'OCCUPIED'"));

            // 6. Tổng số khoang dịch vụ
            stats.put("totalBays", countQuery(conn, "SELECT COUNT(*) FROM service_bays"));

        } catch (SQLException e) {
            System.err.println("[ReportDAO] Loi truy van thong ke Dashboard: " + e.getMessage());
        } finally {
            close(conn, null, null);
        }

        return stats;
    }

    /**
     * Lấy danh sách trạng thái các khoang dịch vụ hiện tại.
     */
    public List<Map<String, String>> getServiceBaysOverview() {
        List<Map<String, String>> bays = new ArrayList<>();
        String sql = "SELECT bay_id, bay_name, bay_type, status FROM service_bays ORDER BY bay_id ASC";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            if (conn != null) {
                stmt = conn.prepareStatement(sql);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    Map<String, String> bay = new HashMap<>();
                    bay.put("bayId", String.valueOf(rs.getInt("bay_id")));
                    bay.put("bayName", rs.getString("bay_name"));
                    bay.put("bayType", rs.getString("bay_type"));
                    bay.put("status", rs.getString("status"));
                    bays.add(bay);
                }
            }
        } catch (SQLException e) {
            System.err.println("[ReportDAO] Loi truy van danh sach khoang: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }

        return bays;
    }

    private int countQuery(Connection conn, String sql) {
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            // Bang chua ton tai hoac rong
        }
        return 0;
    }
}
