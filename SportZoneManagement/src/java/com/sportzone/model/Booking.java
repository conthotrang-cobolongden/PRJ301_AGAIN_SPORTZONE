package com.sportzone.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Booking {

    public static final String STATUS_PENDING = "Pending";
    public static final String STATUS_CONFIRMED = "Confirmed";
    public static final String STATUS_COMPLETED = "Completed";
    public static final String STATUS_CANCELLED = "Cancelled";
    public static final String STATUS_NO_SHOW = "NoShow";

    private int bookingId;
    private int accountId;
    private int timeSlotId;
    private BigDecimal courtPriceAtOrder;
    private BigDecimal serviceTotalAtOrder;
    private BigDecimal totalAmount;
    private String status;
    private String notes;
    private String cancelReason;
    private Timestamp bookingDate;
    private Timestamp updatedAt;

    public boolean isActiveStatus() {
        return STATUS_PENDING.equalsIgnoreCase(status)
                || STATUS_CONFIRMED.equalsIgnoreCase(status)
                || STATUS_COMPLETED.equalsIgnoreCase(status)
                || STATUS_NO_SHOW.equalsIgnoreCase(status);
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getTimeSlotId() {
        return timeSlotId;
    }

    public void setTimeSlotId(int timeSlotId) {
        this.timeSlotId = timeSlotId;
    }

    public BigDecimal getCourtPriceAtOrder() {
        return courtPriceAtOrder;
    }

    public void setCourtPriceAtOrder(BigDecimal courtPriceAtOrder) {
        this.courtPriceAtOrder = courtPriceAtOrder;
    }

    public BigDecimal getServiceTotalAtOrder() {
        return serviceTotalAtOrder;
    }

    public void setServiceTotalAtOrder(BigDecimal serviceTotalAtOrder) {
        this.serviceTotalAtOrder = serviceTotalAtOrder;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public Timestamp getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Timestamp bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
