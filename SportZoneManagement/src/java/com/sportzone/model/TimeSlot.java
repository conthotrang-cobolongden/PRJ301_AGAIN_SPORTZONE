package com.sportzone.model;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class TimeSlot {

    public static final String STATUS_AVAILABLE = "Available";
    public static final String STATUS_BOOKED = "Booked";
    public static final String STATUS_CLOSED = "Closed";

    private int timeSlotId;
    private int courtId;
    private Date slotDate;
    private Time startTime;
    private Time endTime;
    private String status;
    private Timestamp createdAt;

    // View flag (U9): slot đã qua giờ trong ngày — servlet đánh dấu, JSP thêm class is-past
    private boolean past;

    public boolean isAvailable() {
        return STATUS_AVAILABLE.equalsIgnoreCase(status);
    }

    public boolean isPast() {
        return past;
    }

    public void setPast(boolean past) {
        this.past = past;
    }

    public int getTimeSlotId() {
        return timeSlotId;
    }

    public void setTimeSlotId(int timeSlotId) {
        this.timeSlotId = timeSlotId;
    }

    public int getCourtId() {
        return courtId;
    }

    public void setCourtId(int courtId) {
        this.courtId = courtId;
    }

    public Date getSlotDate() {
        return slotDate;
    }

    public void setSlotDate(Date slotDate) {
        this.slotDate = slotDate;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
