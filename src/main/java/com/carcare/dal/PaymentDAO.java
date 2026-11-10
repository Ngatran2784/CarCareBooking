package com.carcare.dal;

import com.carcare.model.Payment;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object: PaymentDAO
 */
public class PaymentDAO extends DBContext {

    // TODO: Triển khai truy vấn JDBC khi CSDL hoàn thiện
    public Payment findById(int paymentId) {
        return null;
    }

    public List<Payment> findAll() {
        return new ArrayList<>();
    }

    public boolean insert(Payment item) {
        return false;
    }

    public boolean update(Payment item) {
        return false;
    }

    public boolean delete(int paymentId) {
        return false;
    }
}
