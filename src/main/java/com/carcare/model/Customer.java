package com.carcare.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Model Entity: Customer
 * Đại diện cho hồ sơ khách hàng trong hệ thống (Bảng `customers`).
 * Có thể liên kết 1-1 với tài khoản `users` qua `userId`.
 */
public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;

    private int customerId;
    private Integer userId;
    private String customerCode;
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private String membershipLevel; // STANDARD, SILVER, GOLD, PLATINUM
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Trường phụ trợ hiển thị
    private int vehicleCount;

    public Customer() {
        this.membershipLevel = "STANDARD";
    }

    public Customer(int customerId, String fullName, String phone, String email, String membershipLevel) {
        this.customerId = customerId;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.membershipLevel = membershipLevel != null ? membershipLevel : "STANDARD";
    }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getCustomerCode() { return customerCode; }
    public void setCustomerCode(String customerCode) { this.customerCode = customerCode; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getMembershipLevel() { return membershipLevel; }
    public void setMembershipLevel(String membershipLevel) { this.membershipLevel = membershipLevel; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public int getVehicleCount() { return vehicleCount; }
    public void setVehicleCount(int vehicleCount) { this.vehicleCount = vehicleCount; }

    public String getMembershipBadgeClass() {
        if ("PLATINUM".equalsIgnoreCase(membershipLevel)) return "bg-dark text-light border border-light";
        if ("GOLD".equalsIgnoreCase(membershipLevel)) return "bg-warning text-dark";
        if ("SILVER".equalsIgnoreCase(membershipLevel)) return "bg-secondary text-light";
        return "bg-light text-dark border";
    }

    @Override
    public String toString() {
        return "Customer{" + "customerId=" + customerId + ", code='" + customerCode + "', fullName='" + fullName + "', phone='" + phone + "'}";
    }
}
