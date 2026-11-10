package com.carcare.dal;

import com.carcare.model.BookingHistory;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object: BookingHistoryDAO
 */
public class BookingHistoryDAO extends DBContext {

    // TODO: Triển khai truy vấn JDBC khi CSDL hoàn thiện
    public BookingHistory findById(int historyId) {
        return null;
    }

    public List<BookingHistory> findAll() {
        return new ArrayList<>();
    }

    public boolean insert(BookingHistory item) {
        return false;
    }

    public boolean update(BookingHistory item) {
        return false;
    }

    public boolean delete(int historyId) {
        return false;
    }
}
