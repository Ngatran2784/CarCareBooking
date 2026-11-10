package com.carcare.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class VehicleInspection implements Serializable {
    private static final long serialVersionUID = 1L;

    private int inspectionId;
    private int jobId;
    private int inspectorId;
    private Integer odometerKm;
    private String fuelLevel;
    private String exteriorCondition;
    private String belongings;
    private LocalDateTime createdAt;

    public VehicleInspection() {}

    public int getInspectionId() { return inspectionId; }
    public void setInspectionId(int inspectionId) { this.inspectionId = inspectionId; }

    public int getJobId() { return jobId; }
    public void setJobId(int jobId) { this.jobId = jobId; }

    public int getInspectorId() { return inspectorId; }
    public void setInspectorId(int inspectorId) { this.inspectorId = inspectorId; }

    public Integer getOdometerKm() { return odometerKm; }
    public void setOdometerKm(Integer odometerKm) { this.odometerKm = odometerKm; }

    public String getFuelLevel() { return fuelLevel; }
    public void setFuelLevel(String fuelLevel) { this.fuelLevel = fuelLevel; }

    public String getExteriorCondition() { return exteriorCondition; }
    public void setExteriorCondition(String exteriorCondition) { this.exteriorCondition = exteriorCondition; }

    public String getBelongings() { return belongings; }
    public void setBelongings(String belongings) { this.belongings = belongings; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
