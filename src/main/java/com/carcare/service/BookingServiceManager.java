package com.carcare.service;

import com.carcare.dal.BookingDAO;
import com.carcare.model.Booking;
import java.util.List;

/**
 * Quản lý nghiệp vụ Đặt lịch 
 */
public class BookingServiceManager {
    private final BookingDAO bookingDAO;

    public BookingServiceManager() {
        this.bookingDAO = new BookingDAO();
    }

    public BookingServiceManager(BookingDAO bookingDAO) {
        this.bookingDAO = bookingDAO;
    }

    // TODO: Triển khai kiểm tra khung giờ trống và tạo mã đặt lịch tự động
    public boolean createBooking(Booking booking) {
        if (booking == null) return false;
        return bookingDAO.insert(booking);
    }

    public List<Booking> getActiveBookings() {
        return bookingDAO.findAll();
    }
}
