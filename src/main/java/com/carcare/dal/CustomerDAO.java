package com.carcare.dal;

import com.carcare.model.Customer;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object: CustomerDAO
 */
public class CustomerDAO extends DBContext {

    // TODO: Triển khai truy vấn JDBC khi CSDL hoàn thiện (Sprint tương ứng)
    public Customer findById(int customerId) {
        return null;
    }

    public List<Customer> findAll() {
        return new ArrayList<>();
    }

    public boolean insert(Customer item) {
        return false;
    }

    public boolean update(Customer item) {
        return false;
    }

    public boolean delete(int customerId) {
        return false;
    }
}
