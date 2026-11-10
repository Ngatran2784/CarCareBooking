package com.carcare.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ServicePriceHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    private int historyId;
    private int serviceId;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;
    private LocalDateTime effectiveDate;
    private Integer changedBy;

    public ServicePriceHistory() {}

    public int getHistoryId() { return historyId; }
    public void setHistoryId(int historyId) { this.historyId = historyId; }

    public int getServiceId() { return serviceId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }

    public BigDecimal getOldPrice() { return oldPrice; }
    public void setOldPrice(BigDecimal oldPrice) { this.oldPrice = oldPrice; }

    public BigDecimal getNewPrice() { return newPrice; }
    public void setNewPrice(BigDecimal newPrice) { this.newPrice = newPrice; }

    public LocalDateTime getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(LocalDateTime effectiveDate) { this.effectiveDate = effectiveDate; }

    public Integer getChangedBy() { return changedBy; }
    public void setChangedBy(Integer changedBy) { this.changedBy = changedBy; }
}
