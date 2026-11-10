package com.carcare.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Staff implements Serializable {
    private static final long serialVersionUID = 1L;

    private int staffId;
    private int userId;
    private User user;
    private String position;
    private String experienceLevel;
    private String status; // AVAILABLE, BUSY, OFF_DUTY
    private LocalDateTime createdAt;

    public Staff() {}

    public int getStaffId() { return staffId; }
    public void setStaffId(int staffId) { this.staffId = staffId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
