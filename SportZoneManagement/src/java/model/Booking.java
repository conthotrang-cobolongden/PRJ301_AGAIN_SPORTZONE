package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Model cua Bookings; san duoc suy ra qua TimeSlotID, khong luu CourtID. Tag:
 * U10-U13, A19-A24.
 */
public class Booking {

    private int bookingID;
    private int accountID;
    private int timeSlotID;
    private BigDecimal courtPriceAtOrder;
    private BigDecimal serviceTotalAtOrder;
    private BigDecimal totalAmount;
    private String status;
    private String notes;
    private String cancelReason;
    private LocalDateTime bookingDate;
    private LocalDateTime updatedAt;
    private String courtName;
    private String typeName;
    private String typeSlug;
    private String location;
    private LocalDate slotDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String paymentStatus;

    public Booking() {
    }

    public Booking(int bookingID, int accountID, int timeSlotID, BigDecimal courtPriceAtOrder,
            BigDecimal serviceTotalAtOrder, BigDecimal totalAmount, String status, String notes,
            String cancelReason, LocalDateTime bookingDate, LocalDateTime updatedAt) {
        this.bookingID = bookingID;
        this.accountID = accountID;
        this.timeSlotID = timeSlotID;
        this.courtPriceAtOrder = courtPriceAtOrder;
        this.serviceTotalAtOrder = serviceTotalAtOrder;
        this.totalAmount = totalAmount;
        this.status = status;
        this.notes = notes;
        this.cancelReason = cancelReason;
        this.bookingDate = bookingDate;
        this.updatedAt = updatedAt;
    }

    public int getBookingID() {
        return bookingID;
    }

    public void setBookingID(int bookingID) {
        this.bookingID = bookingID;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public int getTimeSlotID() {
        return timeSlotID;
    }

    public void setTimeSlotID(int timeSlotID) {
        this.timeSlotID = timeSlotID;
    }

    public BigDecimal getCourtPriceAtOrder() {
        return courtPriceAtOrder;
    }

    public void setCourtPriceAtOrder(BigDecimal value) {
        this.courtPriceAtOrder = value;
    }

    public BigDecimal getServiceTotalAtOrder() {
        return serviceTotalAtOrder;
    }

    public void setServiceTotalAtOrder(BigDecimal value) {
        this.serviceTotalAtOrder = value;
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

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCourtName() {
        return courtName;
    }

    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeSlug() {
        return typeSlug;
    }

    public void setTypeSlug(String typeSlug) {
        this.typeSlug = typeSlug;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public int getBookingId() {
        return bookingID;
    }

    public int getAccountId() {
        return accountID;
    }

    public int getTimeSlotId() {
        return timeSlotID;
    }

    public java.sql.Date getSlotDateValue() {
        return slotDate == null
                ? null
                : java.sql.Date.valueOf(slotDate);
    }

    public java.sql.Time getStartTimeValue() {
        return startTime == null
                ? null
                : java.sql.Time.valueOf(startTime);
    }

    public java.sql.Time getEndTimeValue() {
        return endTime == null
                ? null
                : java.sql.Time.valueOf(endTime);
    }

    public java.sql.Timestamp getBookingDateValue() {
        return bookingDate == null
                ? null
                : java.sql.Timestamp.valueOf(bookingDate);
    }
}
