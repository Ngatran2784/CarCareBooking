package com.carcare.dal;

import com.carcare.service.CatalogRules;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

/** Persistent catalogue CRUD. Prices and audit entries commit together. */
public class CatalogDAO extends DBContext {
    private List<Map<String,Object>> query(Connection c, String sql, Object... args) throws SQLException {
        try (PreparedStatement p = c.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) p.setObject(i + 1, args[i]);
            try (ResultSet r = p.executeQuery()) {
                List<Map<String,Object>> result = new ArrayList<>();
                while (r.next()) {
                    Map<String,Object> row = new LinkedHashMap<>();
                    for (int i = 1; i <= r.getMetaData().getColumnCount(); i++) {
                        Object value = r.getObject(i);
                        if (value instanceof LocalDateTime dateTime) value = Timestamp.valueOf(dateTime);
                        row.put(r.getMetaData().getColumnLabel(i), value);
                    }
                    result.add(row);
                }
                return result;
            }
        }
    }
    private void execute(Connection c, String sql, Object... args) throws SQLException {
        try (PreparedStatement p = c.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) p.setObject(i + 1, args[i]);
            if (p.executeUpdate() == 0)
                throw new IllegalArgumentException("Dữ liệu không tồn tại. Vui lòng tải lại trang.");
        }
    }
    public List<Map<String,Object>> list(String type) throws SQLException {
        String sql = switch (type) {
            case "services" -> "SELECT * FROM services ORDER BY service_name,service_id";
            case "prices" -> "SELECT h.*,s.service_name,u.full_name FROM service_price_history h JOIN services s ON s.service_id=h.service_id LEFT JOIN users u ON u.user_id=h.changed_by ORDER BY h.effective_date DESC,h.history_id DESC";
            case "bays" -> "SELECT b.*,CASE WHEN EXISTS (SELECT 1 FROM bay_maintenance_schedules m WHERE m.bay_id=b.bay_id AND m.status IN ('SCHEDULED','IN_PROGRESS') AND m.start_time<=CURRENT_TIMESTAMP AND m.end_time>CURRENT_TIMESTAMP) THEN 'MAINTENANCE' ELSE b.status END AS effective_status FROM service_bays b ORDER BY b.bay_name";
            case "maintenance" -> "SELECT m.*,b.bay_name FROM bay_maintenance_schedules m JOIN service_bays b ON b.bay_id=m.bay_id ORDER BY m.start_time DESC,m.maintenance_id DESC";
            default -> throw new IllegalArgumentException("Danh mục không hợp lệ.");
        };
        try (Connection c = getConnection()) { return query(c, sql); }
    }
    public void save(Map<String,String> form, boolean services, int userId) throws SQLException {
        String action = form.getOrDefault("action", "");
        if (!(services ? Set.of("service", "deleteService") : Set.of("bay", "deleteBay", "maintenance", "deleteMaintenance")).contains(action))
            throw new IllegalArgumentException("Thao tác không hợp lệ.");
        int id = CatalogRules.id(form.getOrDefault("id", "0"));
        try (Connection c = getConnection()) {
            c.setAutoCommit(false);
            try {
                // Common lock also serializes with staff skill/operating settings writes.
                if (query(c, "SELECT setting_id FROM operating_settings WHERE setting_key='OPENING_TIME' FOR UPDATE").isEmpty())
                    throw new SQLException("Run operating settings migration 004 first.");
                switch (action) {
                    case "service" -> saveService(c, form, id, userId);
                    case "deleteService" -> deleteService(c, id);
                    case "bay" -> saveBay(c, form, id);
                    case "deleteBay" -> deleteBay(c, id);
                    case "maintenance" -> saveMaintenance(c, form, id);
                    case "deleteMaintenance" -> execute(c, "DELETE FROM bay_maintenance_schedules WHERE maintenance_id=?", id);
                    default -> throw new IllegalArgumentException("Thao tác không hợp lệ.");
                }
                c.commit();
            } catch (SQLException | RuntimeException e) { c.rollback(); throw e; }
        }
    }
    private void saveService(Connection c, Map<String,String> f, int id, int userId) throws SQLException {
        String name = CatalogRules.text(f.get("service_name"), "Tên dịch vụ", 100);
        String description = f.getOrDefault("description", "").trim();
        if (description.length() > 5000) throw new IllegalArgumentException("Mô tả không được quá 5000 ký tự.");
        BigDecimal price = CatalogRules.price(f.get("base_price"));
        int minutes = CatalogRules.duration(f.get("estimated_duration_minutes"));
        String status = CatalogRules.status(f.get("status"), Set.of("ACTIVE", "INACTIVE"));
        if (!query(c, "SELECT service_id FROM services WHERE service_name=? AND service_id<>?", name, id).isEmpty())
            throw new IllegalArgumentException("Tên dịch vụ đã tồn tại.");
        if (id == 0) {
            execute(c, "INSERT INTO services(service_name,description,base_price,estimated_duration_minutes,status) VALUES(?,?,?,?,?)", name, description, price, minutes, status);
        } else {
            Map<String,Object> old = require(c, "SELECT * FROM services WHERE service_id=? FOR UPDATE", id);
            execute(c, "UPDATE services SET service_name=?,description=?,base_price=?,estimated_duration_minutes=?,status=? WHERE service_id=?", name, description, price, minutes, status, id);
            if (((BigDecimal)old.get("base_price")).compareTo(price) != 0)
                execute(c, "INSERT INTO service_price_history(service_id,old_price,new_price,changed_by) VALUES(?,?,?,?)", id, old.get("base_price"), price, userId);
        }
    }
    private void deleteService(Connection c, int id) throws SQLException {
        require(c, "SELECT service_id FROM services WHERE service_id=? FOR UPDATE", id);
        if (!query(c, "SELECT service_id FROM booking_services WHERE service_id=? UNION ALL SELECT service_id FROM additional_service_requests WHERE service_id=? UNION ALL SELECT service_id FROM technician_skills WHERE service_id=? UNION ALL SELECT service_id FROM service_price_history WHERE service_id=? LIMIT 1", id, id, id, id).isEmpty())
            throw new IllegalArgumentException("Dịch vụ đã có dữ liệu liên quan hoặc lịch sử giá. Hãy chuyển sang Ngừng cung cấp để giữ lịch sử.");
        execute(c, "DELETE FROM services WHERE service_id=?", id);
    }
    private void saveBay(Connection c, Map<String,String> f, int id) throws SQLException {
        String name = CatalogRules.text(f.get("bay_name"), "Tên khoang", 50);
        String type = CatalogRules.text(f.get("bay_type"), "Loại khoang", 50);
        String status = CatalogRules.status(f.get("status"), Set.of("AVAILABLE", "OCCUPIED", "MAINTENANCE"));
        if (!query(c, "SELECT bay_id FROM service_bays WHERE bay_name=? AND bay_id<>?", name, id).isEmpty())
            throw new IllegalArgumentException("Tên khoang đã tồn tại.");
        if (id == 0) execute(c, "INSERT INTO service_bays(bay_name,bay_type,status) VALUES(?,?,?)", name, type, status);
        else {
            require(c, "SELECT bay_id FROM service_bays WHERE bay_id=? FOR UPDATE", id);
            if (!"OCCUPIED".equals(status) && !query(c, "SELECT assignment_id FROM assignments WHERE bay_id=? AND status IN ('ASSIGNED','IN_PROGRESS') AND start_time<=CURRENT_TIMESTAMP AND estimated_end_time>CURRENT_TIMESTAMP", id).isEmpty())
                throw new IllegalArgumentException("Khoang đang có công việc. Không thể chuyển sang Trống hoặc Bảo trì.");
            if ("OCCUPIED".equals(status) && !query(c, "SELECT maintenance_id FROM bay_maintenance_schedules WHERE bay_id=? AND status IN ('SCHEDULED','IN_PROGRESS') AND start_time<=CURRENT_TIMESTAMP AND end_time>CURRENT_TIMESTAMP", id).isEmpty())
                throw new IllegalArgumentException("Khoang đang trong thời gian bảo trì.");
            execute(c, "UPDATE service_bays SET bay_name=?,bay_type=?,status=? WHERE bay_id=?", name, type, status, id);
        }
    }
    private void deleteBay(Connection c, int id) throws SQLException {
        Map<String,Object> bay = require(c, "SELECT * FROM service_bays WHERE bay_id=? FOR UPDATE", id);
        if ("OCCUPIED".equals(bay.get("status")) || !query(c, "SELECT bay_id FROM assignments WHERE bay_id=? UNION ALL SELECT bay_id FROM bay_maintenance_schedules WHERE bay_id=? LIMIT 1", id, id).isEmpty())
            throw new IllegalArgumentException("Khoang đang sử dụng hoặc có phân công/lịch bảo trì. Không thể xóa khi còn dữ liệu liên quan.");
        execute(c, "DELETE FROM service_bays WHERE bay_id=?", id);
    }
    private void saveMaintenance(Connection c, Map<String,String> f, int id) throws SQLException {
        int bay = CatalogRules.id(f.get("bay_id"));
        Map<String,Object> bayRow = require(c, "SELECT * FROM service_bays WHERE bay_id=? FOR UPDATE", bay);
        String startValue = CatalogRules.text(f.get("start_time"), "Thời điểm bắt đầu", 30);
        String endValue = CatalogRules.text(f.get("end_time"), "Thời điểm kết thúc", 30);
        LocalDateTime start = LocalDateTime.parse(startValue), end = LocalDateTime.parse(endValue);
        CatalogRules.maintenance(start, end);
        String reason = CatalogRules.text(f.get("reason"), "Lý do bảo trì", 255);
        String status = CatalogRules.status(f.get("status"), Set.of("SCHEDULED", "IN_PROGRESS", "COMPLETED", "CANCELLED"));
        if (id != 0) require(c, "SELECT maintenance_id FROM bay_maintenance_schedules WHERE maintenance_id=? FOR UPDATE", id);
        if (Set.of("SCHEDULED", "IN_PROGRESS").contains(status)) {
            if (!query(c, "SELECT maintenance_id FROM bay_maintenance_schedules WHERE bay_id=? AND maintenance_id<>? AND status IN ('SCHEDULED','IN_PROGRESS') AND start_time<? AND end_time>?", bay, id, end, start).isEmpty())
                throw new IllegalArgumentException("Lịch bảo trì trùng với lịch khác của khoang này.");
            if (!query(c, "SELECT assignment_id FROM assignments WHERE bay_id=? AND status IN ('ASSIGNED','IN_PROGRESS') AND start_time<? AND estimated_end_time>?", bay, end, start).isEmpty())
                throw new IllegalArgumentException("Lịch bảo trì trùng với thời gian thi công đã phân công.");
            LocalDateTime now = LocalDateTime.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh"));
            if ("OCCUPIED".equals(bayRow.get("status")) && !start.isAfter(now) && end.isAfter(now))
                throw new IllegalArgumentException("Khoang đang sử dụng. Hãy giải phóng khoang trước khi bảo trì ngay.");
        }
        if (id == 0) execute(c, "INSERT INTO bay_maintenance_schedules(bay_id,start_time,end_time,reason,status) VALUES(?,?,?,?,?)", bay, start, end, reason, status);
        else execute(c, "UPDATE bay_maintenance_schedules SET bay_id=?,start_time=?,end_time=?,reason=?,status=? WHERE maintenance_id=?", bay, start, end, reason, status, id);
    }
    private Map<String,Object> require(Connection c, String sql, int id) throws SQLException {
        List<Map<String,Object>> rows = query(c, sql, id);
        if (rows.isEmpty()) throw new IllegalArgumentException("Dữ liệu không tồn tại. Vui lòng tải lại trang.");
        return rows.get(0);
    }
}
