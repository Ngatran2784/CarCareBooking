package com.carcare.dal;

import com.carcare.model.AdditionalServiceRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object: AdditionalServiceDAO
 */
public class AdditionalServiceDAO extends DBContext {

    // TODO: Triển khai truy vấn JDBC khi CSDL hoàn thiện (Sprint tương ứng)
    public AdditionalServiceRequest findById(int requestId) {
        return null;
    }

    public List<AdditionalServiceRequest> findAll() {
        return new ArrayList<>();
    }

    public boolean insert(AdditionalServiceRequest item) {
        return false;
    }

    public boolean update(AdditionalServiceRequest item) {
        return false;
    }

    public boolean delete(int requestId) {
        return false;
    }
}
