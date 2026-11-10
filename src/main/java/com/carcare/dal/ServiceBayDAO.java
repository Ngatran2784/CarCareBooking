package com.carcare.dal;

import com.carcare.model.ServiceBay;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object: ServiceBayDAO
 */
public class ServiceBayDAO extends DBContext {

    // TODO: Triển khai truy vấn JDBC khi CSDL hoàn thiện
    public ServiceBay findById(int bayId) {
        return null;
    }

    public List<ServiceBay> findAll() {
        return new ArrayList<>();
    }

    public boolean insert(ServiceBay item) {
        return false;
    }

    public boolean update(ServiceBay item) {
        return false;
    }

    public boolean delete(int bayId) {
        return false;
    }
}
