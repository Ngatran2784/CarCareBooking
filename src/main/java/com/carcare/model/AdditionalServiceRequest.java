package com.carcare.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AdditionalServiceRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private int requestId;
    private int jobId;
    private int serviceId;
    private String reason;
    private BigDecimal estimatedCost;
    private String customerApproved; // PENDING, APPROVED, REJECTED
    private LocalDateTime approvalTime;
    private LocalDateTime createdAt;

    public AdditionalServiceRequest() {}

    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }

    public int getJobId() { return jobId; }
    public void setJobId(int jobId) { this.jobId = jobId; }

    public int getServiceId() { return serviceId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public BigDecimal getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(BigDecimal estimatedCost) { this.estimatedCost = estimatedCost; }

    public String getCustomerApproved() { return customerApproved; }
    public void setCustomerApproved(String customerApproved) { this.customerApproved = customerApproved; }

    public LocalDateTime getApprovalTime() { return approvalTime; }
    public void setApprovalTime(LocalDateTime approvalTime) { this.approvalTime = approvalTime; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
