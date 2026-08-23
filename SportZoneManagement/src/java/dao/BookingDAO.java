package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import model.Booking;
import model.BookingService;
import model.BookingSlot;

/**
 * Persistence cho Phase 5 booking.
 *
 * Khong tao invoice.
 * Khong luu CourtID vao Bookings.
 */
public class BookingDAO {

    private static final String BOOKING_SELECT
            = "SELECT "
            + "b.BookingID, "
            + "b.AccountID, "
            + "b.TimeSlotID, "
            + "b.CourtPriceAtOrder, "
            + "b.ServiceTotalAtOrder, "
            + "b.TotalAmount, "
            + "b.Status, "
            + "b.CheckInAt, "
            + "b.Notes, "
            + "b.CancelReason, "
            + "b.BookingDate, "
            + "b.UpdatedAt, "
            + "a.FullName AS RawAccountName, "
            + "r.RoleName AS AccountRoleName, "
            + "c.CourtName, "
            + "ct.TypeName, "
            + "LOWER(ct.TypeName) AS TypeSlug, "
            + "c.Location, "
            + "ts.SlotDate, "
            + "ts.StartTime, "
            + "ts.EndTime, "
            + "i.PaymentStatus "
            + "FROM Bookings b "
            + "INNER JOIN Accounts a ON b.AccountID=a.AccountID "
            + "INNER JOIN Roles r ON a.RoleID=r.RoleID "
            + "INNER JOIN TimeSlots ts "
            + "ON b.TimeSlotID = ts.TimeSlotID "
            + "INNER JOIN Courts c "
            + "ON ts.CourtID = c.CourtID "
            + "INNER JOIN CourtTypes ct "
            + "ON c.CourtTypeID = ct.CourtTypeID "
            + "LEFT JOIN Invoices i "
            + "ON b.BookingID = i.BookingID ";

    public Connection openConnection() throws SQLException {
        return DBUtils.getConnection();
    }

    /**
     * Lock account trong transaction tao booking.
     */
    public String findAccountStatusForUpdate(
            Connection connection,
            int accountID
    ) throws SQLException {
        String sql
                = "SELECT Status "
                + "FROM Accounts WITH (UPDLOCK, HOLDLOCK) "
                + "WHERE AccountID = ?";

        try (PreparedStatement statement
                = connection.prepareStatement(sql)) {

            statement.setInt(1, accountID);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next()
                        ? resultSet.getString("Status")
                        : null;
            }
        }
    }

    /**
     * Lock slot va court de tranh hai request cung dat mot slot.
     */
    public BookingSlot findSlotForUpdate(
            Connection connection,
            int timeSlotID
    ) throws SQLException {
        String sql
                = "SELECT "
                + "ts.TimeSlotID, "
                + "ts.CourtID, "
                + "ts.SlotDate, "
                + "ts.StartTime, "
                + "ts.EndTime, "
                + "ts.Status AS SlotStatus, "
                + "c.Status AS CourtStatus, "
                + "c.IsActive AS CourtActive, "
                + "ct.IsActive AS CourtTypeActive, "
                + "c.PricePerHour AS CourtPrice "
                + "FROM TimeSlots ts WITH (UPDLOCK, HOLDLOCK) "
                + "INNER JOIN Courts c WITH (UPDLOCK, HOLDLOCK) "
                + "ON ts.CourtID = c.CourtID "
                + "INNER JOIN CourtTypes ct "
                + "ON c.CourtTypeID = ct.CourtTypeID "
                + "WHERE ts.TimeSlotID = ?";

        try (PreparedStatement statement
                = connection.prepareStatement(sql)) {

            statement.setInt(1, timeSlotID);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next()
                        ? mapBookingSlot(resultSet)
                        : null;
            }
        }
    }

    /**
     * Insert Booking va tra ve BookingID.
     *
     * Rule 1: U10 chi insert Bookings + BookingServices,
     * khong insert Invoice.
     *
     * Rule 6: Bookings chi nhan TimeSlotID, khong nhan CourtID.
     */
    public int insertBooking(
            Connection connection,
            Booking booking
    ) throws SQLException {
        String sql
                = "INSERT INTO Bookings ("
                + "AccountID, "
                + "TimeSlotID, "
                + "CourtPriceAtOrder, "
                + "ServiceTotalAtOrder, "
                + "TotalAmount, "
                + "Status, "
                + "Notes"
                + ") VALUES (?, ?, ?, ?, ?, N'Pending', ?)";

        try (PreparedStatement statement
                = connection.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS
                )) {

            statement.setInt(
                    1,
                    booking.getAccountID()
            );
            statement.setInt(
                    2,
                    booking.getTimeSlotID()
            );
            statement.setBigDecimal(
                    3,
                    booking.getCourtPriceAtOrder()
            );
            statement.setBigDecimal(
                    4,
                    booking.getServiceTotalAtOrder()
            );
            statement.setBigDecimal(
                    5,
                    booking.getTotalAmount()
            );
            statement.setString(
                    6,
                    booking.getNotes()
            );

            statement.executeUpdate();

            try (ResultSet generatedKeys
                    = statement.getGeneratedKeys()) {

                if (!generatedKeys.next()) {
                    throw new SQLException(
                            "Booking insert returned no generated key"
                    );
                }

                return generatedKeys.getInt(1);
            }
        }
    }

    /**
     * Luu snapshot gia dich vu.
     */
    public void insertBookingService(
            Connection connection,
            BookingService bookingService
    ) throws SQLException {
        String sql
                = "INSERT INTO BookingServices ("
                + "BookingID, ServiceID, Quantity, PriceAtOrder"
                + ") VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement
                = connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    bookingService.getBookingID()
            );
            statement.setInt(
                    2,
                    bookingService.getServiceID()
            );
            statement.setInt(
                    3,
                    bookingService.getQuantity()
            );
            statement.setBigDecimal(
                    4,
                    bookingService.getPriceAtOrder()
            );

            statement.executeUpdate();
        }
    }

    /**
     * Chi Available moi duoc chuyen thanh Booked.
     */
    public boolean markSlotBooked(
            Connection connection,
            int timeSlotID
    ) throws SQLException {
        String sql
                = "UPDATE TimeSlots "
                + "SET Status = N'Booked' "
                + "WHERE TimeSlotID = ? "
                + "AND Status = N'Available'";

        try (PreparedStatement statement
                = connection.prepareStatement(sql)) {

            statement.setInt(1, timeSlotID);
            return statement.executeUpdate() == 1;
        }
    }

    /**
     * U11: chi lay booking thuoc account dang dang nhap.
     */
    public List<Booking> findByAccountID(
            int accountID
    ) throws SQLException {
        String sql
                = BOOKING_SELECT
                + "WHERE b.AccountID = ? "
                + "ORDER BY ts.SlotDate DESC, "
                + "ts.StartTime DESC, b.BookingID DESC";

        List<Booking> bookings = new ArrayList<>();

        try (Connection connection = DBUtils.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(sql)) {

            statement.setInt(1, accountID);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    bookings.add(
                            mapBooking(resultSet)
                    );
                }
            }
        }

        return bookings;
    }

    /**
     * U12 ownership: BookingID va AccountID phai cung khop.
     */
    public Booking findDetailByIDAndAccountID(
            int bookingID,
            int accountID
    ) throws SQLException {
        String sql
                = BOOKING_SELECT
                + "WHERE b.BookingID = ? "
                + "AND b.AccountID = ?";

        try (Connection connection = DBUtils.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(sql)) {

            statement.setInt(1, bookingID);
            statement.setInt(2, accountID);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next()
                        ? mapBooking(resultSet)
                        : null;
            }
        }
    }

    /**
     * Lay service lines cua booking, van kiem tra ownership.
     */
    public List<BookingService>
            findServicesByBookingIDAndAccountID(
                    int bookingID,
                    int accountID
            ) throws SQLException {
        String sql
                = "SELECT "
                + "bs.BookingID, "
                + "bs.ServiceID, "
                + "bs.Quantity, "
                + "bs.PriceAtOrder, "
                + "bs.LineTotal, "
                + "s.ServiceName "
                + "FROM BookingServices bs "
                + "INNER JOIN Bookings b "
                + "ON bs.BookingID = b.BookingID "
                + "INNER JOIN Services s "
                + "ON bs.ServiceID = s.ServiceID "
                + "WHERE bs.BookingID = ? "
                + "AND b.AccountID = ? "
                + "ORDER BY s.ServiceName";

        List<BookingService> serviceLines
                = new ArrayList<>();

        try (Connection connection = DBUtils.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(sql)) {

            statement.setInt(1, bookingID);
            statement.setInt(2, accountID);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    serviceLines.add(
                            mapBookingService(resultSet)
                    );
                }
            }
        }

        return serviceLines;
    }

    /**
     * SQL Server duplicate key:
     * 2601 = duplicate unique index.
     * 2627 = duplicate unique constraint.
     */
    public boolean isActiveSlotConflict(
            SQLException exception
    ) {
        SQLException current = exception;

        while (current != null) {
            int errorCode = current.getErrorCode();

            if (errorCode == 2601 || errorCode == 2627) {
                String message = current.getMessage();

                if (message == null
                        || message.toUpperCase(Locale.ROOT)
                                .contains(
                                        "UQ_BOOKINGS_SLOT_ACTIVE"
                                )) {
                    return true;
                }
            }

            current = current.getNextException();
        }

        return false;
    }

    private BookingSlot mapBookingSlot(
            ResultSet resultSet
    ) throws SQLException {
        BookingSlot slot = new BookingSlot();

        slot.setTimeSlotID(
                resultSet.getInt("TimeSlotID")
        );
        slot.setCourtID(
                resultSet.getInt("CourtID")
        );

        java.sql.Date slotDate
                = resultSet.getDate("SlotDate");

        if (slotDate != null) {
            slot.setSlotDate(
                    slotDate.toLocalDate()
            );
        }

        java.sql.Time startTime
                = resultSet.getTime("StartTime");

        if (startTime != null) {
            slot.setStartTime(
                    startTime.toLocalTime()
            );
        }

        java.sql.Time endTime
                = resultSet.getTime("EndTime");

        if (endTime != null) {
            slot.setEndTime(
                    endTime.toLocalTime()
            );
        }

        slot.setSlotStatus(
                resultSet.getString("SlotStatus")
        );
        slot.setCourtStatus(
                resultSet.getString("CourtStatus")
        );
        slot.setCourtActive(
                resultSet.getBoolean("CourtActive")
        );
        slot.setCourtTypeActive(
                resultSet.getBoolean("CourtTypeActive")
        );
        slot.setCourtPrice(
                resultSet.getBigDecimal("CourtPrice")
        );

        return slot;
    }

    private Booking mapBooking(
            ResultSet resultSet
    ) throws SQLException {
        Booking booking = new Booking();

        booking.setBookingID(
                resultSet.getInt("BookingID")
        );
        booking.setAccountID(
                resultSet.getInt("AccountID")
        );
        booking.setTimeSlotID(
                resultSet.getInt("TimeSlotID")
        );
        booking.setCourtPriceAtOrder(
                resultSet.getBigDecimal("CourtPriceAtOrder")
        );
        booking.setServiceTotalAtOrder(
                resultSet.getBigDecimal("ServiceTotalAtOrder")
        );
        booking.setTotalAmount(
                resultSet.getBigDecimal("TotalAmount")
        );
        booking.setStatus(
                resultSet.getString("Status")
        );
        Timestamp checkInAt = resultSet.getTimestamp("CheckInAt");
        if (checkInAt != null) {
            booking.setCheckInAt(checkInAt.toLocalDateTime());
        }
        booking.setNotes(
                resultSet.getString("Notes")
        );
        booking.setCancelReason(
                resultSet.getString("CancelReason")
        );

        Timestamp bookingDate
                = resultSet.getTimestamp("BookingDate");

        if (bookingDate != null) {
            booking.setBookingDate(
                    bookingDate.toLocalDateTime()
            );
        }

        Timestamp updatedAt
                = resultSet.getTimestamp("UpdatedAt");

        if (updatedAt != null) {
            booking.setUpdatedAt(
                    updatedAt.toLocalDateTime()
            );
        }

        booking.setCourtName(
                resultSet.getString("CourtName")
        );
        booking.setTypeName(
                resultSet.getString("TypeName")
        );
        booking.setTypeSlug(
                resultSet.getString("TypeSlug")
        );
        booking.setLocation(
                resultSet.getString("Location")
        );

        java.sql.Date slotDate
                = resultSet.getDate("SlotDate");

        if (slotDate != null) {
            booking.setSlotDate(
                    slotDate.toLocalDate()
            );
        }

        java.sql.Time startTime
                = resultSet.getTime("StartTime");

        if (startTime != null) {
            booking.setStartTime(
                    startTime.toLocalTime()
            );
        }

        java.sql.Time endTime
                = resultSet.getTime("EndTime");

        if (endTime != null) {
            booking.setEndTime(
                    endTime.toLocalTime()
            );
        }

        booking.setPaymentStatus(
                resultSet.getString("PaymentStatus")
        );
        String roleName = resultSet.getString("AccountRoleName");
        booking.setAccountRoleName(roleName);
        booking.setAccountName("Admin".equals(roleName)
                ? "Khách vãng lai"
                : resultSet.getString("RawAccountName"));

        return booking;
    }

    private BookingService mapBookingService(
            ResultSet resultSet
    ) throws SQLException {
        BookingService serviceLine
                = new BookingService();

        serviceLine.setBookingID(
                resultSet.getInt("BookingID")
        );
        serviceLine.setServiceID(
                resultSet.getInt("ServiceID")
        );
        serviceLine.setQuantity(
                resultSet.getInt("Quantity")
        );
        serviceLine.setPriceAtOrder(
                resultSet.getBigDecimal("PriceAtOrder")
        );
        serviceLine.setLineTotal(
                resultSet.getBigDecimal("LineTotal")
        );
        serviceLine.setServiceName(
                resultSet.getString("ServiceName")
        );

        return serviceLine;
    }
}
