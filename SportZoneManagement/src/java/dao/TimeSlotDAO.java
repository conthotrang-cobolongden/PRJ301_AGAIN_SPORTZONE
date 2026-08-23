package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.TimeSlot;

/**
 * Truy van lich slot cua mot san theo ngay cho khu vuc public.
 *
 * DAO tra ve day du cac trang thai Available, Booked va Closed
 * de court/detail.jsp hien thi lich truc quan.
 *
 * DAO khong tinh thuoc tinh past. CourtDetailServlet se tinh past
 * dua tren ngay va gio hien tai sau khi doc du lieu.
 *
 * Tag: U9
 */
public class TimeSlotDAO {

    /**
     * Lay tat ca slot cua mot san trong mot ngay.
     *
     * Khong loc Status vi JSP can hien thi:
     * - Available: co the chon.
     * - Booked: da co nguoi dat.
     * - Closed: khong phuc vu.
     *
     * @param courtID ID san can xem lich
     * @param slotDate ngay can xem
     * @return danh sach slot sap xep tang dan theo StartTime
     * @throws IllegalArgumentException neu slotDate la null
     * @throws SQLException neu truy van database that bai
     *
     * Tag: U9
     */
    public List<TimeSlot> listByCourtAndDate(
            int courtID,
            LocalDate slotDate
    ) throws SQLException {
        if (slotDate == null) {
            throw new IllegalArgumentException(
                    "Slot date must not be null"
            );
        }

        String sql
                = "SELECT "
                + "ts.TimeSlotID, "
                + "ts.CourtID, "
                + "ts.SlotDate, "
                + "ts.StartTime, "
                + "ts.EndTime, "
                + "ts.Status, "
                + "ts.CreatedAt "
                + "FROM TimeSlots ts "
                + "INNER JOIN Courts c "
                + "ON ts.CourtID = c.CourtID "
                + "INNER JOIN CourtTypes ct "
                + "ON c.CourtTypeID = ct.CourtTypeID "
                + "WHERE ts.CourtID = ? "
                + "AND ts.SlotDate = ? "
                + "AND c.IsActive = 1 "
                + "AND ct.IsActive = 1 "
                + "ORDER BY ts.StartTime";

        List<TimeSlot> slots = new ArrayList<>();

        try (Connection connection = DBUtils.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(sql)) {
            statement.setInt(1, courtID);
            statement.setDate(
                    2,
                    java.sql.Date.valueOf(slotDate)
            );

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    slots.add(mapTimeSlot(resultSet));
                }
            }
        }

        return slots;
    }

    public GenerationResult generateWeek(LocalDate monday)
            throws SQLException {
        String sql = ";WITH Days AS ("
                + "SELECT CAST(? AS DATE) AS SlotDate,0 AS DayOffset "
                + "UNION ALL SELECT DATEADD(DAY,1,SlotDate),DayOffset+1 "
                + "FROM Days WHERE DayOffset<6), Hours AS ("
                + "SELECT CAST(N'06:00:00' AS TIME) StartTime,CAST(N'07:00:00' AS TIME) EndTime "
                + "UNION ALL SELECT N'07:00:00',N'08:00:00' "
                + "UNION ALL SELECT N'08:00:00',N'09:00:00' "
                + "UNION ALL SELECT N'16:00:00',N'17:00:00' "
                + "UNION ALL SELECT N'17:00:00',N'18:00:00' "
                + "UNION ALL SELECT N'18:00:00',N'19:00:00' "
                + "UNION ALL SELECT N'19:00:00',N'20:00:00' "
                + "UNION ALL SELECT N'20:00:00',N'21:00:00') "
                + "INSERT INTO TimeSlots(CourtID,SlotDate,StartTime,EndTime,Status) "
                + "SELECT c.CourtID,d.SlotDate,h.StartTime,h.EndTime,"
                + "CASE WHEN c.Status=N'Available' THEN N'Available' ELSE N'Closed' END "
                + "FROM Courts c CROSS JOIN Days d CROSS JOIN Hours h "
                + "WHERE c.IsActive=1 AND NOT EXISTS (SELECT 1 FROM TimeSlots existing "
                + "WHERE existing.CourtID=c.CourtID AND existing.SlotDate=d.SlotDate "
                + "AND existing.StartTime=h.StartTime) OPTION(MAXRECURSION 7)";
        try (Connection connection = DBUtils.getConnection()) {
            int oldIsolation = connection.getTransactionIsolation();
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            connection.setAutoCommit(false);
            try {
                int created;
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setDate(1, java.sql.Date.valueOf(monday));
                    created = statement.executeUpdate();
                }
                connection.commit();
                return new GenerationResult(created, Math.max(0, 560 - created));
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
                connection.setTransactionIsolation(oldIsolation);
            }
        }
    }

    public static final class GenerationResult {
        private final int created;
        private final int skipped;

        public GenerationResult(int created, int skipped) {
            this.created = created;
            this.skipped = skipped;
        }
        public int getCreated() { return created; }
        public int getSkipped() { return skipped; }
    }

    /**
     * Map mot dong TimeSlots sang model TimeSlot.
     *
     * SQL Server:
     * - DATE     -> java.sql.Date -> LocalDate
     * - TIME(0)  -> java.sql.Time -> LocalTime
     * - DATETIME -> Timestamp -> LocalDateTime
     */
    private TimeSlot mapTimeSlot(ResultSet resultSet)
            throws SQLException {
        TimeSlot slot = new TimeSlot();

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

        slot.setStatus(
                resultSet.getString("Status")
        );

        Timestamp createdAt
                = resultSet.getTimestamp("CreatedAt");

        if (createdAt != null) {
            slot.setCreatedAt(
                    createdAt.toLocalDateTime()
            );
        }

        /*
         * Khong set past tai DAO.
         * Gia tri mac dinh false duoc servlet cap nhat sau.
         */
        return slot;
    }
}
