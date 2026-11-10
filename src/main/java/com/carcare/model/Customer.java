package com.carcare.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;

    private int customerId;
    private Integer userId;
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private String membershipLevel; // STANDARD, SILVER, GOLD, PLATINUM
    private LocalDateTime createdAt;

    public Customer() {}

    public Customer(int customerId, String fullName, String phone, String email, String membershipLevel) {
        this.customerId = customerId;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.membershipLevel = membershipLevel;
    }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

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

    @Override
    public String toString() {
        return "Customer{" + "customerId=" + customerId + ", fullName='" + fullName + "', phone='" + phone + "'}";
    }
}
