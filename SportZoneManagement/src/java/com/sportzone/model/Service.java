package com.sportzone.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Model cua bang Services. Tag: U10, A15. */
public class Service {
    private int serviceID;
    private String serviceName;
    private String description;
    private BigDecimal price;
    private int durationMinutes;
    private boolean active;
    private LocalDateTime createdAt;

    public Service() { }
    public Service(int serviceID, String serviceName, String description, BigDecimal price,
            int durationMinutes, boolean active, LocalDateTime createdAt) {
        this.serviceID = serviceID; this.serviceName = serviceName; this.description = description;
        this.price = price; this.durationMinutes = durationMinutes; this.active = active; this.createdAt = createdAt;
    }
    public int getServiceID() { return serviceID; }
    public void setServiceID(int serviceID) { this.serviceID = serviceID; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
