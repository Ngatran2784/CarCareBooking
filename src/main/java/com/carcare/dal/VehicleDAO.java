package com.carcare.dal;

import com.carcare.model.Vehicle;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object: VehicleDAO
 */
public class VehicleDAO extends DBContext {

    // TODO: Triển khai truy vấn JDBC khi CSDL hoàn thiện
    public Vehicle findById(int vehicleId) {
        return null;
    }

    public List<Vehicle> findAll() {
        return new ArrayList<>();
    }

    public boolean insert(Vehicle item) {
        return false;
    }

    public boolean update(Vehicle item) {
        return false;
    }

    public boolean delete(int vehicleId) {
        return false;
    }
}
