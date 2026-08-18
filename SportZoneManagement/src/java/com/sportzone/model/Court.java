package com.sportzone.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Model cua bang Courts, kem thong tin loai san phuc vu view. Tag: U7, U8, A8. */
public class Court {
    private int courtID;
    private int courtTypeID;
    private String courtName;
    private String location;
    private BigDecimal pricePerHour;
    private String description;
    private String imageURL;
    private String status;
    private boolean active;
    private LocalDateTime createdAt;
    private String typeName;
    private String typeSlug;

    public Court() { }
    public Court(int courtID, int courtTypeID, String courtName, String location, BigDecimal pricePerHour,
            String description, String imageURL, String status, boolean active, LocalDateTime createdAt) {
        this.courtID = courtID; this.courtTypeID = courtTypeID; this.courtName = courtName;
        this.location = location; this.pricePerHour = pricePerHour; this.description = description;
        this.imageURL = imageURL; this.status = status; this.active = active; this.createdAt = createdAt;
    }
    public int getCourtID() { return courtID; }
    public void setCourtID(int courtID) { this.courtID = courtID; }
    public int getCourtTypeID() { return courtTypeID; }
    public void setCourtTypeID(int courtTypeID) { this.courtTypeID = courtTypeID; }
    public String getCourtName() { return courtName; }
    public void setCourtName(String courtName) { this.courtName = courtName; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public BigDecimal getPricePerHour() { return pricePerHour; }
    public void setPricePerHour(BigDecimal pricePerHour) { this.pricePerHour = pricePerHour; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImageURL() { return imageURL; }
    public void setImageURL(String imageURL) { this.imageURL = imageURL; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }
    public String getTypeSlug() { return typeSlug; }
    public void setTypeSlug(String typeSlug) { this.typeSlug = typeSlug; }
}
