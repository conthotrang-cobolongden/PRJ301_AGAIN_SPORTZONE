package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.Booking;
import model.BookingService;

public class AdminBookingDAO {

    private static final String SELECT = "SELECT b.BookingID,b.AccountID,"
            + "b.TimeSlotID,b.CourtPriceAtOrder,b.ServiceTotalAtOrder,"
            + "b.TotalAmount,b.Status,b.CheckInAt,b.Notes,b.CancelReason,"
            + "b.BookingDate,b.UpdatedAt,c.CourtName,ct.TypeName,"
            + "LOWER(ct.TypeName) AS TypeSlug,c.Location,ts.SlotDate,"
            + "ts.StartTime,ts.EndTime,i.PaymentStatus,"
            + "a.FullName AS RawAccountName,r.RoleName AS AccountRoleName "
            + "FROM Bookings b JOIN Accounts a ON b.AccountID=a.AccountID "
            + "JOIN Roles r ON a.RoleID=r.RoleID "
            + "JOIN TimeSlots ts ON b.TimeSlotID=ts.TimeSlotID "
            + "JOIN Courts c ON ts.CourtID=c.CourtID "
            + "JOIN CourtTypes ct ON c.CourtTypeID=ct.CourtTypeID "
            + "LEFT JOIN Invoices i ON b.BookingID=i.BookingID ";

    public int reconcile(LocalDateTime now) throws SQLException {
        String noShow = "UPDATE b SET Status = N'NoShow', UpdatedAt=? "
                + "FROM Bookings b JOIN TimeSlots ts ON b.TimeSlotID=ts.TimeSlotID "
                + "WHERE b.Status=N'Confirmed' AND DATEADD(MINUTE,30,"
                + "DATEADD(SECOND,DATEDIFF(SECOND,CAST('00:00:00' AS TIME),"
                + "ts.StartTime),CAST(ts.SlotDate AS DATETIME)))<=?";
        String completed = "UPDATE b SET Status = N'Completed', UpdatedAt=? "
                + "FROM Bookings b JOIN TimeSlots ts ON b.TimeSlotID=ts.TimeSlotID "
                + "WHERE b.Status = N'CheckedIn' AND DATEADD(SECOND,"
                + "DATEDIFF(SECOND,CAST('00:00:00' AS TIME),ts.EndTime),"
                + "CAST(ts.SlotDate AS DATETIME))<=?";
        try (Connection connection = DBUtils.getConnection()) {
            connection.setAutoCommit(false);
            try {
                int changed = executeTimedUpdate(connection, noShow, now)
                        + executeTimedUpdate(connection, completed, now);
                connection.commit();
                return changed;
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private int executeTimedUpdate(Connection connection, String sql,
            LocalDateTime now) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            Timestamp timestamp = Timestamp.valueOf(now);
            statement.setTimestamp(1, timestamp);
            statement.setTimestamp(2, timestamp);
            return statement.executeUpdate();
        }
    }

    public List<Booking> findAll(String status) throws SQLException {
        String sql = SELECT + (status == null ? "" : "WHERE b.Status=? ")
                + "ORDER BY ts.SlotDate DESC,ts.StartTime DESC,b.BookingID DESC";
        List<Booking> bookings = new ArrayList<>();
        try (Connection connection = DBUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            if (status != null) statement.setString(1, status);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) bookings.add(mapBooking(rs));
            }
        }
        return bookings;
    }

    public Booking findDetail(int bookingID) throws SQLException {
        String sql = SELECT + "WHERE b.BookingID=?";
        try (Connection connection = DBUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookingID);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? mapBooking(rs) : null;
            }
        }
    }

    public List<BookingService> findServices(int bookingID)
            throws SQLException {
        String sql = "SELECT bs.BookingID,bs.ServiceID,bs.Quantity,"
                + "bs.PriceAtOrder,bs.LineTotal,s.ServiceName "
                + "FROM BookingServices bs JOIN Services s "
                + "ON bs.ServiceID=s.ServiceID WHERE bs.BookingID=? "
                + "ORDER BY s.ServiceName";
        List<BookingService> lines = new ArrayList<>();
        try (Connection connection = DBUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookingID);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    BookingService line = new BookingService();
                    line.setBookingID(rs.getInt("BookingID"));
                    line.setServiceID(rs.getInt("ServiceID"));
                    line.setQuantity(rs.getInt("Quantity"));
                    line.setPriceAtOrder(rs.getBigDecimal("PriceAtOrder"));
                    line.setLineTotal(rs.getBigDecimal("LineTotal"));
                    line.setServiceName(rs.getString("ServiceName"));
                    lines.add(line);
                }
            }
        }
        return lines;
    }

    public boolean checkIn(int bookingID, LocalDateTime earliest,
            LocalDateTime deadline, LocalDateTime now) throws SQLException {
        String sql = "UPDATE Bookings SET Status = N'CheckedIn',CheckInAt=?,"
                + "UpdatedAt=? WHERE BookingID=? AND Status=N'Confirmed' "
                + "AND ?>=? AND ?<?";
        try (Connection connection = DBUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            Timestamp nowValue = Timestamp.valueOf(now);
            statement.setTimestamp(1, nowValue);
            statement.setTimestamp(2, nowValue);
            statement.setInt(3, bookingID);
            statement.setTimestamp(4, nowValue);
            statement.setTimestamp(5, Timestamp.valueOf(earliest));
            statement.setTimestamp(6, nowValue);
            statement.setTimestamp(7, Timestamp.valueOf(deadline));
            return statement.executeUpdate() == 1;
        }
    }

    private Booking mapBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setBookingID(rs.getInt("BookingID"));
        booking.setAccountID(rs.getInt("AccountID"));
        booking.setTimeSlotID(rs.getInt("TimeSlotID"));
        booking.setCourtPriceAtOrder(rs.getBigDecimal("CourtPriceAtOrder"));
        booking.setServiceTotalAtOrder(rs.getBigDecimal("ServiceTotalAtOrder"));
        booking.setTotalAmount(rs.getBigDecimal("TotalAmount"));
        booking.setStatus(rs.getString("Status"));
        Timestamp checkInAt = rs.getTimestamp("CheckInAt");
        if (checkInAt != null) booking.setCheckInAt(checkInAt.toLocalDateTime());
        booking.setNotes(rs.getString("Notes"));
        booking.setCancelReason(rs.getString("CancelReason"));
        Timestamp bookingDate = rs.getTimestamp("BookingDate");
        if (bookingDate != null) booking.setBookingDate(bookingDate.toLocalDateTime());
        Timestamp updatedAt = rs.getTimestamp("UpdatedAt");
        if (updatedAt != null) booking.setUpdatedAt(updatedAt.toLocalDateTime());
        booking.setCourtName(rs.getString("CourtName"));
        booking.setTypeName(rs.getString("TypeName"));
        booking.setTypeSlug(rs.getString("TypeSlug"));
        booking.setLocation(rs.getString("Location"));
        java.sql.Date slotDate = rs.getDate("SlotDate");
        if (slotDate != null) booking.setSlotDate(slotDate.toLocalDate());
        java.sql.Time start = rs.getTime("StartTime");
        if (start != null) booking.setStartTime(start.toLocalTime());
        java.sql.Time end = rs.getTime("EndTime");
        if (end != null) booking.setEndTime(end.toLocalTime());
        booking.setPaymentStatus(rs.getString("PaymentStatus"));
        String role = rs.getString("AccountRoleName");
        booking.setAccountRoleName(role);
        booking.setAccountName("Admin".equals(role)
                ? "Khách vãng lai" : rs.getString("RawAccountName"));
        return booking;
    }
}
