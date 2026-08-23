package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Du lieu slot + court dung de kiem tra luc tao booking.
 * Day khong phai mot bang rieng trong database.
 */
public class BookingSlot {

    private int timeSlotID;
    private int courtID;
    private LocalDate slotDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String slotStatus;
    private String courtStatus;
    private boolean courtActive;
    private boolean courtTypeActive;
    private BigDecimal courtPrice;

    public int getTimeSlotID() {
        return timeSlotID;
    }

    public void setTimeSlotID(int timeSlotID) {
        this.timeSlotID = timeSlotID;
    }

    public int getCourtID() {
        return courtID;
    }

    public void setCourtID(int courtID) {
        this.courtID = courtID;
    }

    public LocalDate getSlotDate() {
        return slotDate;
    }

    public void setSlotDate(LocalDate slotDate) {
        this.slotDate = slotDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getSlotStatus() {
        return slotStatus;
    }

    public void setSlotStatus(String slotStatus) {
        this.slotStatus = slotStatus;
    }

    public String getCourtStatus() {
        return courtStatus;
    }

    public void setCourtStatus(String courtStatus) {
        this.courtStatus = courtStatus;
    }

    public boolean isCourtActive() {
        return courtActive;
    }

    public void setCourtActive(boolean courtActive) {
        this.courtActive = courtActive;
    }

    public boolean isCourtTypeActive() {
        return courtTypeActive;
    }

    public void setCourtTypeActive(boolean courtTypeActive) {
        this.courtTypeActive = courtTypeActive;
    }

    public BigDecimal getCourtPrice() {
        return courtPrice;
    }

    public void setCourtPrice(BigDecimal courtPrice) {
        this.courtPrice = courtPrice;
    }
}