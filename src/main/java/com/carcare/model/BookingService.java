package com.carcare.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class BookingService implements Serializable {
    private static final long serialVersionUID = 1L;

    private int bookingServiceId;
    private int bookingId;
    private int serviceId;
    private Service service;
    private BigDecimal price;
    private String notes;

    public BookingService() {}

    public int getBookingServiceId() { return bookingServiceId; }
    public void setBookingServiceId(int bookingServiceId) { this.bookingServiceId = bookingServiceId; }

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public int getServiceId() { return serviceId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }

    public Service getService() { return service; }
    public void setService(Service service) { this.service = service; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
