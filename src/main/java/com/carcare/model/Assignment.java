package com.carcare.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Assignment implements Serializable {
    private static final long serialVersionUID = 1L;

    private int assignmentId;
    private int bookingId;
    private int bayId;
    private ServiceBay serviceBay;
    private int technicianId;
    private Staff technician;
    private LocalDateTime startTime;
    private LocalDateTime estimatedEndTime;
    private LocalDateTime actualEndTime;
    private String status; // ASSIGNED, IN_PROGRESS, COMPLETED, REASSIGNED

    public Assignment() {}

    public int getAssignmentId() { return assignmentId; }
    public void setAssignmentId(int assignmentId) { this.assignmentId = assignmentId; }

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public int getBayId() { return bayId; }
    public void setBayId(int bayId) { this.bayId = bayId; }

    public ServiceBay getServiceBay() { return serviceBay; }
    public void setServiceBay(ServiceBay serviceBay) { this.serviceBay = serviceBay; }

    public int getTechnicianId() { return technicianId; }
    public void setTechnicianId(int technicianId) { this.technicianId = technicianId; }

    public Staff getTechnician() { return technician; }
    public void setTechnician(Staff technician) { this.technician = technician; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEstimatedEndTime() { return estimatedEndTime; }
    public void setEstimatedEndTime(LocalDateTime estimatedEndTime) { this.estimatedEndTime = estimatedEndTime; }

    public LocalDateTime getActualEndTime() { return actualEndTime; }
    public void setActualEndTime(LocalDateTime actualEndTime) { this.actualEndTime = actualEndTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
