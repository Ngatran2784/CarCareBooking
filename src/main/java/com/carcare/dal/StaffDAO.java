package com.carcare.dal;

import com.carcare.model.Staff;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object: StaffDAO
 */
public class StaffDAO extends DBContext {

    // TODO: Triển khai truy vấn JDBC khi CSDL hoàn thiện (Sprint tương ứng)
    public Staff findById(int staffId) {
        return null;
    }

    public List<Staff> findAll() {
        return new ArrayList<>();
    }

    public boolean insert(Staff item) {
        return false;
    }

    public boolean update(Staff item) {
        return false;
    }

    public boolean delete(int staffId) {
        return false;
    }
}
