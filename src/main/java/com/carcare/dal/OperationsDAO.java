package com.carcare.dal;

import com.carcare.service.OperatingRules;
import java.sql.*;
import java.time.*;
import java.util.*;

/** JDBC operations; all writes are transactional and serialize on the settings row. */
public class OperationsDAO extends DBContext {
    private List<Map<String,Object>> rows(Connection c, String sql, Object... args) throws SQLException {
        try (PreparedStatement p=c.prepareStatement(sql)) {
            for(int i=0;i<args.length;i++) p.setObject(i+1,args[i]);
            try(ResultSet r=p.executeQuery()) {
                List<Map<String,Object>> result=new ArrayList<>();
                while(r.next()) {
                    Map<String,Object> row=new LinkedHashMap<>();
                    for(int i=1;i<=r.getMetaData().getColumnCount();i++) row.put(r.getMetaData().getColumnLabel(i),r.getObject(i));
                    result.add(row);
                }
                return result;
            }
        }
    }
    public List<Map<String,Object>> list(String type) throws SQLException {
        String sql=switch(type) {
            case "technicians" -> "SELECT s.staff_id, u.full_name FROM staff s JOIN users u ON u.user_id=s.user_id WHERE u.status='ACTIVE' AND EXISTS (SELECT 1 FROM user_roles ur JOIN roles r ON r.role_id=ur.role_id WHERE ur.user_id=u.user_id AND r.role_name='TECHNICIAN') ORDER BY u.full_name";
            case "services" -> "SELECT service_id, service_name FROM services WHERE status='ACTIVE' ORDER BY service_name";
            case "shifts" -> "SELECT w.*,u.full_name FROM work_shifts w JOIN staff s ON s.staff_id=w.staff_id JOIN users u ON u.user_id=s.user_id ORDER BY w.shift_date DESC,w.start_time,u.full_name";
            case "skills" -> "SELECT t.*,u.full_name,v.service_name FROM technician_skills t JOIN staff s ON s.staff_id=t.staff_id JOIN users u ON u.user_id=s.user_id JOIN services v ON v.service_id=t.service_id ORDER BY u.full_name,v.service_name";
            case "holidays" -> "SELECT * FROM holiday_schedules ORDER BY holiday_date";
            default -> throw new IllegalArgumentException("Danh mục không hợp lệ.");
        };
        try(Connection c=getConnection()) { return rows(c,sql); }
    }
    public Map<String,String> settings() throws SQLException {
        try(Connection c=getConnection()) { return settings(c); }
    }
    private Map<String,String> settings(Connection c) throws SQLException {
        Map<String,String> result=new HashMap<>();
        for(Map<String,Object> r:rows(c,"SELECT setting_key,setting_value FROM operating_settings"))
            result.put(r.get("setting_key").toString(),r.get("setting_value").toString());
        if(!result.keySet().containsAll(List.of("OPENING_TIME","CLOSING_TIME","BUFFER_MINUTES","SLOT_INTERVAL_MINUTES")))
            throw new SQLException("Missing operating settings; run migration 004.");
        return result;
    }
    private void execute(Connection c,String sql,Object...args) throws SQLException {
        try(PreparedStatement p=c.prepareStatement(sql)) {
            for(int i=0;i<args.length;i++) p.setObject(i+1,args[i]);
            if(p.executeUpdate()==0) throw new IllegalArgumentException("Dữ liệu không tồn tại hoặc đã được thay đổi. Vui lòng tải lại trang.");
        }
    }
    public void save(Map<String,String> f, boolean staffPage) throws SQLException {
        String requestedAction=f.getOrDefault("action", "");
        List<String> required=switch(requestedAction) {
            case "shift" -> List.of("staffId","date","start","end","status");
            case "skill" -> List.of("staffId","serviceId","level");
            case "hours" -> List.of("opening","closing","buffer","interval");
            case "holiday" -> List.of("date","name");
            case "deleteShift", "deleteSkill", "deleteHoliday" -> List.of("id");
            default -> throw new IllegalArgumentException("Thao tác không hợp lệ.");
        };
        for(String key:required) if(f.get(key)==null || f.get(key).isBlank())
            throw new IllegalArgumentException("Vui lòng nhập đầy đủ các trường bắt buộc.");
        try(Connection c=getConnection()) {
            c.setAutoCommit(false);
            try {
                if(rows(c,"SELECT setting_id FROM operating_settings WHERE setting_key='OPENING_TIME' FOR UPDATE").isEmpty())
                    throw new SQLException("Missing settings migration");
                String action=f.get("action");
                int id=number(f.getOrDefault("id","0"));
                if(staffPage) saveStaff(c,f,action,id); else saveSettings(c,f,action,id);
                c.commit();
            } catch(SQLException|RuntimeException e) { c.rollback(); throw e; }
        }
    }
    private void saveStaff(Connection c,Map<String,String> f,String action,int id) throws SQLException {
        if("deleteShift".equals(action)) { execute(c,"DELETE FROM work_shifts WHERE shift_id=?",id); return; }
        if("deleteSkill".equals(action)) { execute(c,"DELETE FROM technician_skills WHERE skill_id=?",id); return; }
        if(!List.of("shift","skill").contains(action)) throw new IllegalArgumentException("Thao tác không hợp lệ.");
        int staff=number(f.get("staffId"));
        if(rows(c,"SELECT s.staff_id FROM staff s JOIN users u ON u.user_id=s.user_id WHERE s.staff_id=? AND u.status='ACTIVE' AND EXISTS (SELECT 1 FROM user_roles ur JOIN roles r ON r.role_id=ur.role_id WHERE ur.user_id=u.user_id AND r.role_name='TECHNICIAN')",staff).isEmpty())
            throw new IllegalArgumentException("Vui lòng chọn kỹ thuật viên đang hoạt động.");
        if("skill".equals(action)) {
            int service=number(f.get("serviceId"));
            String level=f.get("level");
            if(!List.of("BEGINNER","INTERMEDIATE","ADVANCED","EXPERT").contains(level)) throw new IllegalArgumentException("Trình độ không hợp lệ.");
            if(rows(c,"SELECT service_id FROM services WHERE service_id=? AND status='ACTIVE'",service).isEmpty()) throw new IllegalArgumentException("Dịch vụ không hoạt động.");
            if(!rows(c,"SELECT skill_id FROM technician_skills WHERE staff_id=? AND service_id=? AND skill_id<>?",staff,service,id).isEmpty()) throw new IllegalArgumentException("Kỹ năng đã tồn tại. Hãy sửa trình độ ở dòng tương ứng.");
            if(id==0) execute(c,"INSERT INTO technician_skills(staff_id,service_id,proficiency_level) VALUES(?,?,?)",staff,service,level);
            else execute(c,"UPDATE technician_skills SET staff_id=?,service_id=?,proficiency_level=? WHERE skill_id=?",staff,service,level,id);
        } else {
            LocalDate date=LocalDate.parse(f.get("date"));
            LocalTime start=LocalTime.parse(f.get("start")),end=LocalTime.parse(f.get("end"));
            String status=f.get("status");
            if(!List.of("SCHEDULED","ATTENDED","ABSENT").contains(status)) throw new IllegalArgumentException("Trạng thái ca không hợp lệ.");
            Map<String,String> cfg=settings(c);
            OperatingRules.shift(start,end,LocalTime.parse(cfg.get("OPENING_TIME")),LocalTime.parse(cfg.get("CLOSING_TIME")),!rows(c,"SELECT holiday_id FROM holiday_schedules WHERE holiday_date=? AND is_closed=1",date).isEmpty());
            if(!"ABSENT".equals(status) && !rows(c,"SELECT shift_id FROM work_shifts WHERE staff_id=? AND shift_date=? AND status<>'ABSENT' AND shift_id<>? AND start_time<? AND end_time>?",staff,date,id,end,start).isEmpty())
                throw new IllegalArgumentException("Ca bị trùng với ca làm việc khác của kỹ thuật viên.");
            if(id==0) execute(c,"INSERT INTO work_shifts(staff_id,shift_date,start_time,end_time,status) VALUES(?,?,?,?,?)",staff,date,start,end,status);
            else execute(c,"UPDATE work_shifts SET staff_id=?,shift_date=?,start_time=?,end_time=?,status=? WHERE shift_id=?",staff,date,start,end,status,id);
        }
    }
    private void saveSettings(Connection c,Map<String,String> f,String action,int id) throws SQLException {
        if("hours".equals(action)) {
            LocalTime opening=LocalTime.parse(f.get("opening")),closing=LocalTime.parse(f.get("closing"));
            int buffer=number(f.get("buffer")),interval=number(f.get("interval"));
            OperatingRules.hours(opening,closing,buffer,interval);
            if(!rows(c,"SELECT shift_id FROM work_shifts WHERE shift_date>=CURRENT_DATE AND status<>'ABSENT' AND (start_time<? OR end_time>?)",opening,closing).isEmpty())
                throw new IllegalArgumentException("Giờ mới nằm ngoài ca đã lập. Hãy điều chỉnh các ca hiện tại/tương lai trước.");
            String[] keys={"OPENING_TIME","CLOSING_TIME","BUFFER_MINUTES","SLOT_INTERVAL_MINUTES"};
            String[] values={opening.toString(),closing.toString(),""+buffer,""+interval};
            for(int i=0;i<keys.length;i++) execute(c,"UPDATE operating_settings SET setting_value=? WHERE setting_key=?",values[i],keys[i]);
        } else if("holiday".equals(action)) {
            LocalDate date=LocalDate.parse(f.get("date"));
            String name=f.getOrDefault("name","").trim();
            if(name.isEmpty()||name.length()>100) throw new IllegalArgumentException("Tên ngày nghỉ phải có từ 1 đến 100 ký tự.");
            if(!rows(c,"SELECT shift_id FROM work_shifts WHERE shift_date=? AND status<>'ABSENT'",date).isEmpty()) throw new IllegalArgumentException("Ngày này đã có ca làm việc. Hãy điều chỉnh ca trước khi đặt ngày nghỉ.");
            execute(c,"INSERT INTO holiday_schedules(holiday_date,holiday_name,is_closed) VALUES(?,?,1) ON DUPLICATE KEY UPDATE holiday_name=VALUES(holiday_name),is_closed=1",date,name);
        } else if("deleteHoliday".equals(action)) execute(c,"DELETE FROM holiday_schedules WHERE holiday_id=?",id);
        else throw new IllegalArgumentException("Thao tác không hợp lệ.");
    }
    private int number(String value) {
        try { int n=Integer.parseInt(value); if(n<0) throw new NumberFormatException(); return n; }
        catch(Exception e) { throw new IllegalArgumentException("Giá trị số không hợp lệ."); }
    }
}
