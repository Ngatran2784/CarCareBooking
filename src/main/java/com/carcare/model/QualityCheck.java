package com.carcare.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class QualityCheck implements Serializable {
    private static final long serialVersionUID = 1L;

    private int qcId;
    private int jobId;
    private int checkerId;
    private String status; // PASSED, FAILED, REWORK_REQUIRED
    private String checklistResult;
    private String notes;
    private LocalDateTime checkedAt;

    public QualityCheck() {}

    public int getQcId() { return qcId; }
    public void setQcId(int qcId) { this.qcId = qcId; }

    public int getJobId() { return jobId; }
    public void setJobId(int jobId) { this.jobId = jobId; }

    public int getCheckerId() { return checkerId; }
    public void setCheckerId(int checkerId) { this.checkerId = checkerId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getChecklistResult() { return checklistResult; }
    public void setChecklistResult(String checklistResult) { this.checklistResult = checklistResult; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCheckedAt() { return checkedAt; }
    public void setCheckedAt(LocalDateTime checkedAt) { this.checkedAt = checkedAt; }
}
