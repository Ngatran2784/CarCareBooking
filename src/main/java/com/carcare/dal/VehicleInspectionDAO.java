package com.carcare.dal;

import com.carcare.model.VehicleInspection;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object: VehicleInspectionDAO
 */
public class VehicleInspectionDAO extends DBContext {

    // TODO: Triển khai truy vấn JDBC khi CSDL hoàn thiện
    public VehicleInspection findById(int inspectionId) {
        return null;
    }

    public List<VehicleInspection> findAll() {
        return new ArrayList<>();
    }

    public boolean insert(VehicleInspection item) {
        return false;
    }

    public boolean update(VehicleInspection item) {
        return false;
    }

    public boolean delete(int inspectionId) {
        return false;
    }
}
