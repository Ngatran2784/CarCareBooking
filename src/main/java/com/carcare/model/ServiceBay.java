package com.carcare.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ServiceBay implements Serializable {
    private static final long serialVersionUID = 1L;

    private int bayId;
    private String bayName;
    private String bayType;
    private String status; // AVAILABLE, OCCUPIED, MAINTENANCE
    private LocalDateTime createdAt;

    public ServiceBay() {}

    public ServiceBay(int bayId, String bayName, String bayType, String status) {
        this.bayId = bayId;
        this.bayName = bayName;
        this.bayType = bayType;
        this.status = status;
    }

    public int getBayId() { return bayId; }
    public void setBayId(int bayId) { this.bayId = bayId; }

    public String getBayName() { return bayName; }
    public void setBayName(String bayName) { this.bayName = bayName; }

    public String getBayType() { return bayType; }
    public void setBayType(String bayType) { this.bayType = bayType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
