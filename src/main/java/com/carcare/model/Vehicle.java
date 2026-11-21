package com.carcare.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Model Entity: Vehicle
 * Đại diện cho phương tiện xe của khách hàng (Bảng `vehicles`).
 * Liên kết N - 1 với `customers` qua `customerId`.
 */
public class Vehicle implements Serializable {
    private static final long serialVersionUID = 1L;

    private int vehicleId;
    private int customerId;
    private String licensePlate;
    private String brand;
    private String model;
    private Integer year;
    private String color;
    private String vehicleType; // SEDAN, SUV, HATCHBACK, TRUCK, VAN
    private LocalDateTime createdAt;

    // Trường phụ trợ hiển thị (Customer Info)
    private String customerName;
    private String customerPhone;
    private String customerCode;

    public Vehicle() {
        this.vehicleType = "SEDAN";
    }

    public Vehicle(int vehicleId, int customerId, String licensePlate, String brand, String model) {
        this.vehicleId = vehicleId;
        this.customerId = customerId;
        this.licensePlate = licensePlate;
        this.brand = brand;
        this.model = model;
        this.vehicleType = "SEDAN";
    }

    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getCustomerCode() { return customerCode; }
    public void setCustomerCode(String customerCode) { this.customerCode = customerCode; }

    /**
     * Tên hiển thị đầy đủ phục vụ dropdown chọn xe (S2-07 Booking)
     */
    public String getDisplayName() {
        return brand + " " + model + " (" + licensePlate + ")";
    }

    public String getTypeBadgeClass() {
        if ("SUV".equalsIgnoreCase(vehicleType)) return "bg-primary";
        if ("SEDAN".equalsIgnoreCase(vehicleType)) return "bg-info text-dark";
        if ("HATCHBACK".equalsIgnoreCase(vehicleType)) return "bg-success";
        if ("TRUCK".equalsIgnoreCase(vehicleType)) return "bg-warning text-dark";
        return "bg-secondary";
    }

    @Override
    public String toString() {
        return "Vehicle{" + "vehicleId=" + vehicleId + ", customerId=" + customerId + ", licensePlate='" + licensePlate + "'}";
    }
}
