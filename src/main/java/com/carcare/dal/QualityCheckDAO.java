package com.carcare.dal;

import com.carcare.model.QualityCheck;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object: QualityCheckDAO
 */
public class QualityCheckDAO extends DBContext {

    // TODO: Triển khai truy vấn JDBC khi CSDL hoàn thiện
    public QualityCheck findById(int qcId) {
        return null;
    }

    public List<QualityCheck> findAll() {
        return new ArrayList<>();
    }

    public boolean insert(QualityCheck item) {
        return false;
    }

    public boolean update(QualityCheck item) {
        return false;
    }

    public boolean delete(int qcId) {
        return false;
    }
}
