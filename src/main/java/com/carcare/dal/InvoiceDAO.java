package com.carcare.dal;

import com.carcare.model.Invoice;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object: InvoiceDAO
 */
public class InvoiceDAO extends DBContext {

    // TODO: Triển khai truy vấn JDBC khi CSDL hoàn thiện (Sprint tương ứng)
    public Invoice findById(int invoiceId) {
        return null;
    }

    public List<Invoice> findAll() {
        return new ArrayList<>();
    }

    public boolean insert(Invoice item) {
        return false;
    }

    public boolean update(Invoice item) {
        return false;
    }

    public boolean delete(int invoiceId) {
        return false;
    }
}
