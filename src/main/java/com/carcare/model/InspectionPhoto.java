package com.carcare.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class InspectionPhoto implements Serializable {
    private static final long serialVersionUID = 1L;

    private int photoId;
    private int inspectionId;
    private String photoUrl;
    private String caption;
    private LocalDateTime createdAt;

    public InspectionPhoto() {}

    public int getPhotoId() { return photoId; }
    public void setPhotoId(int photoId) { this.photoId = photoId; }

    public int getInspectionId() { return inspectionId; }
    public void setInspectionId(int inspectionId) { this.inspectionId = inspectionId; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
