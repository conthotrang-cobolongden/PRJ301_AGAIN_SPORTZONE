package service;

import dao.BookingDAO;
import dao.InvoiceDAO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.BookingService;
import model.Invoice;

/** Business rules cho thanh toan gia lap va hoa don User Phase 6. */
public class InvoiceService {

    private final InvoiceDAO invoiceDAO;
    private final BookingDAO bookingDAO;

    public InvoiceService() {
        this(new InvoiceDAO(), new BookingDAO());
    }

    public InvoiceService(InvoiceDAO invoiceDAO, BookingDAO bookingDAO) {
        if (invoiceDAO == null || bookingDAO == null) {
            throw new IllegalArgumentException("Invoice dependencies must not be null");
        }
        this.invoiceDAO = invoiceDAO;
        this.bookingDAO = bookingDAO;
    }

    public Invoice startPayment(int bookingID, int accountID) throws SQLException {
        requirePositive(bookingID);
        requirePositive(accountID);
        return invoiceDAO.startPayment(bookingID, accountID);
    }

    public Invoice pay(int bookingID, int accountID) throws SQLException {
        requirePositive(bookingID);
        requirePositive(accountID);
        return invoiceDAO.markPaid(bookingID, accountID);
    }

    public Invoice cancel(int bookingID, int accountID) throws SQLException {
        requirePositive(bookingID);
        requirePositive(accountID);
        return invoiceDAO.cancelPayment(bookingID, accountID);
    }

    public List<Invoice> listPaid(int accountID) throws SQLException {
        requirePositive(accountID);
        return invoiceDAO.findPaidByAccount(accountID);
    }

    public InvoiceDetail getPaidDetail(int bookingID, int accountID)
            throws SQLException {
        requirePositive(bookingID);
        requirePositive(accountID);
        Invoice invoice = invoiceDAO.findPaidByBookingAndAccount(
                bookingID, accountID);
        if (invoice == null) return null;
        return new InvoiceDetail(invoice,
                bookingDAO.findServicesByBookingIDAndAccountID(
                        bookingID, accountID));
    }

    private void requirePositive(int value) {
        if (value <= 0) throw new IllegalArgumentException("ID must be positive");
    }

    public static final class InvoiceDetail {
        private final Invoice invoice;
        private final List<BookingService> serviceLines;

        private InvoiceDetail(Invoice invoice, List<BookingService> serviceLines) {
            this.invoice = invoice;
            this.serviceLines = Collections.unmodifiableList(
                    new ArrayList<>(serviceLines));
        }

        public Invoice getInvoice() { return invoice; }
        public List<BookingService> getServiceLines() { return serviceLines; }
    }
}
