package com.carcare.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Vehicle implements Serializable {
    private static final long serialVersionUID = 1L;

    private int vehicleId;
    private int customerId;
    private String licensePlate;
    private String brand;
    private String model;
    private Integer year;
    private String color;
    private String vehicleType; // SEDAN, SUV, HATCHBACK, etc.
    private LocalDateTime createdAt;

    public Vehicle() {}

    public Vehicle(int vehicleId, int customerId, String licensePlate, String brand, String model) {
        this.vehicleId = vehicleId;
        this.customerId = customerId;
        this.licensePlate = licensePlate;
        this.brand = brand;
        this.model = model;
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

    @Override
    public String toString() {
        return "Vehicle{" + "vehicleId=" + vehicleId + ", licensePlate='" + licensePlate + "'}";
    }
}
