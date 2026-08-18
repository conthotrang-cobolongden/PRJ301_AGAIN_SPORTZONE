package model;

import java.math.BigDecimal;

/** Model cua BookingServices voi khoa ghep BookingID + ServiceID. Tag: U10, A20. */
public class BookingService {
    private int bookingID;
    private int serviceID;
    private int quantity;
    private BigDecimal priceAtOrder;
    private BigDecimal lineTotal;
    private String serviceName;

    public BookingService() { }
    public BookingService(int bookingID, int serviceID, int quantity, BigDecimal priceAtOrder) {
        this.bookingID = bookingID; this.serviceID = serviceID;
        this.quantity = quantity; this.priceAtOrder = priceAtOrder;
    }
    public int getBookingID() { return bookingID; }
    public void setBookingID(int bookingID) { this.bookingID = bookingID; }
    public int getServiceID() { return serviceID; }
    public void setServiceID(int serviceID) { this.serviceID = serviceID; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getPriceAtOrder() { return priceAtOrder; }
    public void setPriceAtOrder(BigDecimal priceAtOrder) { this.priceAtOrder = priceAtOrder; }
    public BigDecimal getLineTotal() { return lineTotal; }
    public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
}
