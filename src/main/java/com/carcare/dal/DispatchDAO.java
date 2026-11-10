package com.carcare.dal;

import com.carcare.model.Assignment;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object: DispatchDAO
 */
public class DispatchDAO extends DBContext {

    // TODO: Triển khai truy vấn JDBC khi CSDL hoàn thiện
    public Assignment findById(int assignmentId) {
        return null;
    }

    public List<Assignment> findAll() {
        return new ArrayList<>();
    }

    public boolean insert(Assignment item) {
        return false;
    }

    public boolean update(Assignment item) {
        return false;
    }

    public boolean delete(int assignmentId) {
        return false;
    }
}
