package com.carcare.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class JobCard implements Serializable {
    private static final long serialVersionUID = 1L;

    private int jobId;
    private int bookingId;
    private Integer assignmentId;
    private String status; // CREATED, INSPECTING, IN_SERVICE, QC_PENDING, QC_PASSED, DONE
    private LocalDateTime startTime;
    private LocalDateTime completedTime;
    private String notes;

    public JobCard() {}

    public int getJobId() { return jobId; }
    public void setJobId(int jobId) { this.jobId = jobId; }

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public Integer getAssignmentId() { return assignmentId; }
    public void setAssignmentId(Integer assignmentId) { this.assignmentId = assignmentId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getCompletedTime() { return completedTime; }
    public void setCompletedTime(LocalDateTime completedTime) { this.completedTime = completedTime; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
