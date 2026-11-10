package com.carcare.dal;

import com.carcare.model.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object: ServiceDAO
 */
public class ServiceDAO extends DBContext {

    // TODO: Triển khai truy vấn JDBC khi CSDL hoàn thiện (Sprint tương ứng)
    public Service findById(int serviceId) {
        return null;
    }

    public List<Service> findAll() {
        return new ArrayList<>();
    }

    public boolean insert(Service item) {
        return false;
    }

    public boolean update(Service item) {
        return false;
    }

    public boolean delete(int serviceId) {
        return false;
    }
}
