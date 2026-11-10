package com.carcare.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class BookingHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    private int historyId;
    private int bookingId;
    private String previousStatus;
    private String newStatus;
    private Integer changedBy;
    private LocalDateTime changeTime;
    private String comment;

    public BookingHistory() {}

    public int getHistoryId() { return historyId; }
    public void setHistoryId(int historyId) { this.historyId = historyId; }

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public String getPreviousStatus() { return previousStatus; }
    public void setPreviousStatus(String previousStatus) { this.previousStatus = previousStatus; }

    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }

    public Integer getChangedBy() { return changedBy; }
    public void setChangedBy(Integer changedBy) { this.changedBy = changedBy; }

    public LocalDateTime getChangeTime() { return changeTime; }
    public void setChangeTime(LocalDateTime changeTime) { this.changeTime = changeTime; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
