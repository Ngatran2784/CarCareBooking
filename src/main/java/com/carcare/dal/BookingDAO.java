package com.carcare.dal;

import com.carcare.model.Booking;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object: BookingDAO
 */
public class BookingDAO extends DBContext {

    // TODO: Triển khai truy vấn JDBC khi CSDL hoàn thiện
    public Booking findById(int bookingId) {
        return null;
    }

    public List<Booking> findAll() {
        return new ArrayList<>();
    }

    public boolean insert(Booking item) {
        return false;
    }

    public boolean update(Booking item) {
        return false;
    }

    public boolean delete(int bookingId) {
        return false;
    }
}
