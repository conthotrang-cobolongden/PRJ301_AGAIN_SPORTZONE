package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/** Model cua Invoices; moi booking co toi da mot hoa don. Tag: U14-U18, A25-A31. */
public class Invoice {
    private int invoiceID;
    private int bookingID;
    private String invoiceCode;
    private LocalDateTime invoiceDate;
    private BigDecimal subTotal;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private String paymentStatus;
    private String paymentMethod;
    private LocalDateTime paidAt;
    private String paymentNote;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String accountName;
    private String courtName;
    private LocalDate slotDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private BigDecimal courtAmount;

    public Invoice() { }
    public Invoice(int invoiceID, int bookingID, String invoiceCode, LocalDateTime invoiceDate,
            BigDecimal subTotal, BigDecimal discountAmount, BigDecimal finalAmount,
            String paymentStatus, String paymentMethod, LocalDateTime paidAt, String paymentNote,
            String note, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.invoiceID = invoiceID; this.bookingID = bookingID; this.invoiceCode = invoiceCode;
        this.invoiceDate = invoiceDate; this.subTotal = subTotal; this.discountAmount = discountAmount;
        this.finalAmount = finalAmount; this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod; this.paidAt = paidAt; this.paymentNote = paymentNote;
        this.note = note; this.createdAt = createdAt; this.updatedAt = updatedAt;
    }
    public int getInvoiceID() { return invoiceID; }
    public void setInvoiceID(int invoiceID) { this.invoiceID = invoiceID; }
    public int getBookingID() { return bookingID; }
    public void setBookingID(int bookingID) { this.bookingID = bookingID; }
    public String getInvoiceCode() { return invoiceCode; }
    public void setInvoiceCode(String invoiceCode) { this.invoiceCode = invoiceCode; }
    public LocalDateTime getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(LocalDateTime invoiceDate) { this.invoiceDate = invoiceDate; }
    public BigDecimal getSubTotal() { return subTotal; }
    public void setSubTotal(BigDecimal subTotal) { this.subTotal = subTotal; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal value) { this.discountAmount = value; }
    public BigDecimal getFinalAmount() { return finalAmount; }
    public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
    public String getPaymentNote() { return paymentNote; }
    public void setPaymentNote(String paymentNote) { this.paymentNote = paymentNote; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
    public String getCourtName() { return courtName; }
    public void setCourtName(String courtName) { this.courtName = courtName; }
    public LocalDate getSlotDate() { return slotDate; }
    public void setSlotDate(LocalDate slotDate) { this.slotDate = slotDate; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public BigDecimal getCourtAmount() { return courtAmount; }
    public void setCourtAmount(BigDecimal courtAmount) { this.courtAmount = courtAmount; }
}
