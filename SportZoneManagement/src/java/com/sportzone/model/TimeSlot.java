package com.sportzone.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/** Model cua bang TimeSlots. Tag: U9, A11. */
public class TimeSlot {
    private int timeSlotID;
    private int courtID;
    private LocalDate slotDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
    private LocalDateTime createdAt;
    private boolean past;

    public TimeSlot() { }
    public TimeSlot(int timeSlotID, int courtID, LocalDate slotDate, LocalTime startTime,
            LocalTime endTime, String status, LocalDateTime createdAt) {
        this.timeSlotID = timeSlotID; this.courtID = courtID; this.slotDate = slotDate;
        this.startTime = startTime; this.endTime = endTime; this.status = status; this.createdAt = createdAt;
    }
    public int getTimeSlotID() { return timeSlotID; }
    public void setTimeSlotID(int timeSlotID) { this.timeSlotID = timeSlotID; }
    public int getCourtID() { return courtID; }
    public void setCourtID(int courtID) { this.courtID = courtID; }
    public LocalDate getSlotDate() { return slotDate; }
    public void setSlotDate(LocalDate slotDate) { this.slotDate = slotDate; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public boolean isPast() { return past; }
    public void setPast(boolean past) { this.past = past; }
}
