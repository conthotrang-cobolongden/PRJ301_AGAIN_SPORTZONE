package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import model.Invoice;

/**
 * Truy van hoa don phuc vu membership va Phase 6.
 */
public class InvoiceDAO {

    private static final String INVOICE_SELECT
            = "SELECT i.InvoiceID, i.BookingID, i.InvoiceCode, "
            + "i.InvoiceDate, i.SubTotal, i.DiscountAmount, "
            + "i.FinalAmount, i.PaymentStatus, i.PaymentMethod, "
            + "i.PaidAt, i.PaymentNote, i.Note, i.CreatedAt, i.UpdatedAt, "
            + "a.FullName AS AccountName, c.CourtName, ts.SlotDate, "
            + "ts.StartTime, ts.EndTime, b.CourtPriceAtOrder AS CourtAmount "
            + "FROM Invoices i "
            + "INNER JOIN Bookings b ON i.BookingID = b.BookingID "
            + "INNER JOIN Accounts a ON b.AccountID = a.AccountID "
            + "INNER JOIN TimeSlots ts ON b.TimeSlotID = ts.TimeSlotID "
            + "INNER JOIN Courts c ON ts.CourtID = c.CourtID ";

    public Invoice startPayment(int bookingID, int accountID)
            throws SQLException {
        try (Connection connection = DBUtils.getConnection()) {
            connection.setAutoCommit(false);
            try {
                expireOwnedUnpaid(connection, accountID);
                Invoice existing = findByBookingAndAccount(
                        connection, bookingID, accountID);
                if (existing != null) {
                    if ("Cancelled".equals(existing.getPaymentStatus())) {
                        cancelBookingAndReleaseSlot(connection, bookingID,
                                accountID, "Payment cancelled");
                    } else if ("Paid".equals(existing.getPaymentStatus())) {
                        confirmPaidBooking(connection, bookingID, accountID);
                    }
                    connection.commit();
                    return existing;
                }

                BigDecimal totalAmount = null;
                String bookingStatus = null;
                String bookingSql = "SELECT TotalAmount, Status FROM Bookings "
                        + "WHERE BookingID = ? AND AccountID = ?";
                try (PreparedStatement statement
                        = connection.prepareStatement(bookingSql)) {
                    statement.setInt(1, bookingID);
                    statement.setInt(2, accountID);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (resultSet.next()) {
                            totalAmount = resultSet.getBigDecimal("TotalAmount");
                            bookingStatus = resultSet.getString("Status");
                        }
                    }
                }

                if (totalAmount == null
                        || (!"Pending".equals(bookingStatus)
                        && !"Confirmed".equals(bookingStatus))) {
                    connection.commit();
                    return null;
                }

                String insertSql = "INSERT INTO Invoices "
                        + "(BookingID, InvoiceCode, SubTotal, DiscountAmount, "
                        + "FinalAmount, PaymentStatus, PaymentMethod, Note) "
                        + "VALUES (?, ?, ?, 0, ?, N'Unpaid', NULL, N'Simulated bank transfer')";
                try (PreparedStatement statement
                        = connection.prepareStatement(insertSql)) {
                    statement.setInt(1, bookingID);
                    statement.setString(2, String.format(
                            Locale.ROOT, "INV-%06d", bookingID));
                    statement.setBigDecimal(3, totalAmount);
                    statement.setBigDecimal(4, totalAmount);
                    statement.executeUpdate();
                }

                Invoice created = findByBookingAndAccount(
                        connection, bookingID, accountID);
                connection.commit();
                return created;
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public Invoice findPayment(int bookingID, int accountID)
            throws SQLException {
        try (Connection connection = DBUtils.getConnection()) {
            connection.setAutoCommit(false);
            try {
                expireOwnedUnpaid(connection, accountID);
                Invoice invoice = findByBookingAndAccount(
                        connection, bookingID, accountID);
                if (invoice != null
                        && "Cancelled".equals(invoice.getPaymentStatus())) {
                    cancelBookingAndReleaseSlot(connection, bookingID,
                            accountID, "Payment cancelled");
                } else if (invoice != null
                        && "Paid".equals(invoice.getPaymentStatus())) {
                    confirmPaidBooking(connection, bookingID, accountID);
                }
                connection.commit();
                return invoice;
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public Invoice markPaid(int bookingID, int accountID)
            throws SQLException {
        try (Connection connection = DBUtils.getConnection()) {
            connection.setAutoCommit(false);
            try {
                expireOwnedUnpaid(connection, accountID);
                String sql = "UPDATE i SET PaymentStatus = N'Paid', "
                        + "PaymentMethod = N'BankTransfer', PaidAt = GETDATE(), "
                        + "UpdatedAt = GETDATE() FROM Invoices i "
                        + "INNER JOIN Bookings b ON i.BookingID = b.BookingID "
                        + "WHERE i.BookingID = ? AND b.AccountID = ? "
                        + "AND i.PaymentStatus = N'Unpaid' "
                        + "AND DATEADD(MINUTE, 15, i.InvoiceDate) > GETDATE()";
                try (PreparedStatement statement
                        = connection.prepareStatement(sql)) {
                    statement.setInt(1, bookingID);
                    statement.setInt(2, accountID);
                    statement.executeUpdate();
                }
                confirmPaidBooking(connection, bookingID, accountID);
                Invoice invoice = findByBookingAndAccount(
                        connection, bookingID, accountID);
                connection.commit();
                return invoice;
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public Invoice cancelPayment(int bookingID, int accountID)
            throws SQLException {
        try (Connection connection = DBUtils.getConnection()) {
            connection.setAutoCommit(false);
            try {
                String sql = "UPDATE i SET PaymentStatus = N'Cancelled', "
                        + "PaymentMethod = NULL, PaidAt = NULL, UpdatedAt = GETDATE() "
                        + "FROM Invoices i "
                        + "INNER JOIN Bookings b ON i.BookingID = b.BookingID "
                        + "WHERE i.BookingID = ? AND b.AccountID = ? "
                        + "AND i.PaymentStatus = N'Unpaid'";
                try (PreparedStatement statement
                        = connection.prepareStatement(sql)) {
                    statement.setInt(1, bookingID);
                    statement.setInt(2, accountID);
                    statement.executeUpdate();
                }

                Invoice invoice = findByBookingAndAccount(
                        connection, bookingID, accountID);
                if (invoice != null
                        && "Cancelled".equals(invoice.getPaymentStatus())) {
                    cancelBookingAndReleaseSlot(connection, bookingID,
                            accountID, "Payment cancelled by user");
                }
                connection.commit();
                return invoice;
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private void confirmPaidBooking(Connection connection, int bookingID,
            int accountID) throws SQLException {
        // Rule 3: Paid stays on Invoice; Confirmed is the booking lifecycle status.
        String sql = "UPDATE b SET Status = N'Confirmed', "
                + "UpdatedAt = GETDATE() FROM Bookings b "
                + "INNER JOIN Invoices i ON b.BookingID = i.BookingID "
                + "WHERE b.BookingID = ? AND b.AccountID = ? "
                + "AND b.Status = N'Pending' "
                + "AND i.PaymentStatus = N'Paid'";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookingID);
            statement.setInt(2, accountID);
            statement.executeUpdate();
        }
    }

    public List<Invoice> findPaidByAccount(int accountID)
            throws SQLException {
        String sql = INVOICE_SELECT
                + "WHERE b.AccountID = ? AND i.PaymentStatus = N'Paid' "
                + "ORDER BY i.PaidAt DESC, i.InvoiceID DESC";
        List<Invoice> invoices = new ArrayList<>();
        try (Connection connection = DBUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, accountID);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    invoices.add(mapInvoice(resultSet));
                }
            }
        }
        return invoices;
    }

    public Invoice findPaidByBookingAndAccount(
            int bookingID, int accountID) throws SQLException {
        String sql = INVOICE_SELECT
                + "WHERE i.BookingID = ? AND b.AccountID = ? "
                + "AND i.PaymentStatus = N'Paid'";
        try (Connection connection = DBUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookingID);
            statement.setInt(2, accountID);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapInvoice(resultSet) : null;
            }
        }
    }

    private void expireOwnedUnpaid(Connection connection, int accountID)
            throws SQLException {
        String cancelBookings = "UPDATE b SET Status = N'Cancelled', "
                + "CancelReason = N'Payment expired', UpdatedAt = GETDATE() "
                + "FROM Bookings b INNER JOIN Invoices i "
                + "ON b.BookingID = i.BookingID "
                + "WHERE b.AccountID = ? AND i.PaymentStatus = N'Unpaid' "
                + "AND b.Status IN (N'Pending', N'Confirmed') "
                + "AND DATEADD(MINUTE, 15, i.InvoiceDate) <= GETDATE()";
        try (PreparedStatement statement
                = connection.prepareStatement(cancelBookings)) {
            statement.setInt(1, accountID);
            statement.executeUpdate();
        }

        String cancelInvoices = "UPDATE i SET PaymentStatus = N'Cancelled', "
                + "UpdatedAt = GETDATE() FROM Invoices i "
                + "INNER JOIN Bookings b ON i.BookingID = b.BookingID "
                + "WHERE b.AccountID = ? AND i.PaymentStatus = N'Unpaid' "
                + "AND DATEADD(MINUTE, 15, i.InvoiceDate) <= GETDATE()";
        try (PreparedStatement statement
                = connection.prepareStatement(cancelInvoices)) {
            statement.setInt(1, accountID);
            statement.executeUpdate();
        }

        releaseCancelledSlots(connection, accountID);
    }

    private void cancelBookingAndReleaseSlot(Connection connection,
            int bookingID, int accountID, String reason) throws SQLException {
        String sql = "UPDATE b SET Status = N'Cancelled', CancelReason = ?, "
                + "UpdatedAt = GETDATE() FROM Bookings b "
                + "INNER JOIN Invoices i ON b.BookingID = i.BookingID "
                + "WHERE b.BookingID = ? AND b.AccountID = ? "
                + "AND i.PaymentStatus = N'Cancelled' "
                + "AND b.Status IN (N'Pending', N'Confirmed')";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, reason);
            statement.setInt(2, bookingID);
            statement.setInt(3, accountID);
            statement.executeUpdate();
        }

        String releaseSql = "UPDATE ts SET Status = N'Available' "
                + "FROM TimeSlots ts INNER JOIN Bookings b "
                + "ON ts.TimeSlotID = b.TimeSlotID "
                + "WHERE b.BookingID = ? AND b.AccountID = ? "
                + "AND b.Status = N'Cancelled' "
                + "AND NOT EXISTS (SELECT 1 FROM Bookings activeBooking "
                + "WHERE activeBooking.TimeSlotID = ts.TimeSlotID "
                + "AND activeBooking.Status IN "
                + "(N'Pending', N'Confirmed', N'Completed', N'NoShow'))";
        try (PreparedStatement statement
                = connection.prepareStatement(releaseSql)) {
            statement.setInt(1, bookingID);
            statement.setInt(2, accountID);
            statement.executeUpdate();
        }
    }

    private void releaseCancelledSlots(Connection connection, int accountID)
            throws SQLException {
        String sql = "UPDATE ts SET Status = N'Available' "
                + "FROM TimeSlots ts WHERE EXISTS (SELECT 1 FROM Bookings b "
                + "INNER JOIN Invoices i ON b.BookingID = i.BookingID "
                + "WHERE b.TimeSlotID = ts.TimeSlotID AND b.AccountID = ? "
                + "AND b.Status = N'Cancelled' "
                + "AND i.PaymentStatus = N'Cancelled') "
                + "AND NOT EXISTS (SELECT 1 FROM Bookings activeBooking "
                + "WHERE activeBooking.TimeSlotID = ts.TimeSlotID "
                + "AND activeBooking.Status IN "
                + "(N'Pending', N'Confirmed', N'Completed', N'NoShow'))";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, accountID);
            statement.executeUpdate();
        }
    }

    private Invoice findByBookingAndAccount(
            Connection connection, int bookingID, int accountID)
            throws SQLException {
        String sql = INVOICE_SELECT
                + "WHERE i.BookingID = ? AND b.AccountID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookingID);
            statement.setInt(2, accountID);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapInvoice(resultSet) : null;
            }
        }
    }

    private Invoice mapInvoice(ResultSet resultSet) throws SQLException {
        Invoice invoice = new Invoice();
        invoice.setInvoiceID(resultSet.getInt("InvoiceID"));
        invoice.setBookingID(resultSet.getInt("BookingID"));
        invoice.setInvoiceCode(resultSet.getString("InvoiceCode"));
        Timestamp invoiceDate = resultSet.getTimestamp("InvoiceDate");
        if (invoiceDate != null) invoice.setInvoiceDate(invoiceDate.toLocalDateTime());
        invoice.setSubTotal(resultSet.getBigDecimal("SubTotal"));
        invoice.setDiscountAmount(resultSet.getBigDecimal("DiscountAmount"));
        invoice.setFinalAmount(resultSet.getBigDecimal("FinalAmount"));
        invoice.setPaymentStatus(resultSet.getString("PaymentStatus"));
        invoice.setPaymentMethod(resultSet.getString("PaymentMethod"));
        Timestamp paidAt = resultSet.getTimestamp("PaidAt");
        if (paidAt != null) invoice.setPaidAt(paidAt.toLocalDateTime());
        invoice.setPaymentNote(resultSet.getString("PaymentNote"));
        invoice.setNote(resultSet.getString("Note"));
        Timestamp createdAt = resultSet.getTimestamp("CreatedAt");
        if (createdAt != null) invoice.setCreatedAt(createdAt.toLocalDateTime());
        Timestamp updatedAt = resultSet.getTimestamp("UpdatedAt");
        if (updatedAt != null) invoice.setUpdatedAt(updatedAt.toLocalDateTime());
        invoice.setAccountName(resultSet.getString("AccountName"));
        invoice.setCourtName(resultSet.getString("CourtName"));
        java.sql.Date slotDate = resultSet.getDate("SlotDate");
        if (slotDate != null) invoice.setSlotDate(slotDate.toLocalDate());
        java.sql.Time startTime = resultSet.getTime("StartTime");
        if (startTime != null) invoice.setStartTime(startTime.toLocalTime());
        java.sql.Time endTime = resultSet.getTime("EndTime");
        if (endTime != null) invoice.setEndTime(endTime.toLocalTime());
        invoice.setCourtAmount(resultSet.getBigDecimal("CourtAmount"));
        return invoice;
    }

    public BigDecimal sumPaidByAccountAndPeriod(
            int accountID,
            LocalDate fromInclusive,
            LocalDate toExclusive
    ) throws SQLException {
        String sql
                = "SELECT COALESCE(SUM(i.FinalAmount), 0) AS TotalPaid "
                + "FROM Invoices i "
                + "INNER JOIN Bookings b ON i.BookingID = b.BookingID "
                + "WHERE b.AccountID = ? "
                // Rule 3: Payment status only belongs to Invoices.
                + "AND i.PaymentStatus = N'Paid' "
                + "AND i.PaidAt >= ? "
                + "AND i.PaidAt < ?";

        try (Connection connection = DBUtils.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(sql)) {

            statement.setInt(1, accountID);
            statement.setTimestamp(
                    2,
                    Timestamp.valueOf(fromInclusive.atStartOfDay())
            );
            statement.setTimestamp(
                    3,
                    Timestamp.valueOf(toExclusive.atStartOfDay())
            );

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return BigDecimal.ZERO;
                }

                BigDecimal total = resultSet.getBigDecimal("TotalPaid");
                return total == null ? BigDecimal.ZERO : total;
            }
        }
    }
}
