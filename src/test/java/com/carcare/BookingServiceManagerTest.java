package com.carcare;

import com.carcare.model.Booking;
import com.carcare.service.BookingServiceManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class BookingServiceManagerTest {

    private BookingServiceManager bookingServiceManager;

    @BeforeEach
    public void setUp() {
        bookingServiceManager = new BookingServiceManager();
    }

    @Test
    @DisplayName("Kiểm tra khởi tạo BookingServiceManager thành công")
    public void testInitialization() {
        Assertions.assertNotNull(bookingServiceManager, "BookingServiceManager khong duoc null");
    }

    @Test
    @DisplayName("Tạo booking với dữ liệu null trả về false")
    public void testCreateBookingWithNull() {
        boolean result = bookingServiceManager.createBooking(null);
        Assertions.assertFalse(result, "Tao booking voi null phai tra ve false");
    }

    @Test
    @DisplayName("Lấy danh sách active bookings không bị null")
    public void testGetActiveBookings() {
        Assertions.assertNotNull(bookingServiceManager.getActiveBookings(), "Danh sach bookings khong duoc null");
    }
}
