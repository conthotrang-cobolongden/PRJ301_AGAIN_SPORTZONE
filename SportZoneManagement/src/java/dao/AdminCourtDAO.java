package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.Court;

public class AdminCourtDAO {

    public List<Court> findAll() throws SQLException {
        String sql = "SELECT c.CourtID,c.CourtTypeID,c.CourtName,c.Location,"
                + "c.PricePerHour,c.Description,c.ImageURL,c.Status,c.IsActive,"
                + "c.CreatedAt,ct.TypeName,LOWER(ct.TypeName) AS TypeSlug "
                + "FROM Courts c JOIN CourtTypes ct ON c.CourtTypeID=ct.CourtTypeID "
                + "WHERE c.IsActive=1 ORDER BY c.CourtID";
        List<Court> courts = new ArrayList<>();
        try (Connection connection = DBUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()) {
            while (rs.next()) courts.add(mapCourt(rs));
        }
        return courts;
    }

    public CourtUpdateOutcome updatePriceAndStatus(int courtID,
            BigDecimal price, String status, LocalDateTime now)
            throws SQLException {
        try (Connection connection = DBUtils.getConnection()) {
            connection.setAutoCommit(false);
            try {
                String lockSql = "SELECT Status FROM Courts WITH (UPDLOCK,HOLDLOCK) "
                        + "WHERE CourtID=? AND IsActive=1";
                boolean found;
                try (PreparedStatement statement = connection.prepareStatement(lockSql)) {
                    statement.setInt(1, courtID);
                    try (ResultSet rs = statement.executeQuery()) {
                        found = rs.next();
                    }
                }
                if (!found) {
                    connection.commit();
                    return new CourtUpdateOutcome(false, false, 0);
                }

                int blocking = 0;
                if (!"Available".equals(status)) {
                    String countSql = "SELECT COUNT(*) AS BlockingCount "
                            + "FROM TimeSlots ts JOIN Bookings b "
                            + "ON ts.TimeSlotID=b.TimeSlotID "
                            + "WHERE ts.CourtID=? AND b.Status IN "
                            + "(N'Pending',N'Confirmed',N'CheckedIn') "
                            + "AND DATEADD(SECOND,DATEDIFF(SECOND,CAST('00:00:00' AS TIME),"
                            + "ts.StartTime),CAST(ts.SlotDate AS DATETIME))>?";
                    try (PreparedStatement statement = connection.prepareStatement(countSql)) {
                        statement.setInt(1, courtID);
                        statement.setTimestamp(2, Timestamp.valueOf(now));
                        try (ResultSet rs = statement.executeQuery()) {
                            if (rs.next()) blocking = rs.getInt("BlockingCount");
                        }
                    }
                }
                if (blocking > 0) {
                    connection.commit();
                    return new CourtUpdateOutcome(true, false, blocking);
                }

                String updateSql = "UPDATE Courts SET PricePerHour=?,Status=? "
                        + "WHERE CourtID=? AND IsActive=1";
                int rows;
                try (PreparedStatement statement = connection.prepareStatement(updateSql)) {
                    statement.setBigDecimal(1, price);
                    statement.setString(2, status);
                    statement.setInt(3, courtID);
                    rows = statement.executeUpdate();
                }
                connection.commit();
                return new CourtUpdateOutcome(true, rows == 1, 0);
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private Court mapCourt(ResultSet rs) throws SQLException {
        Court court = new Court();
        court.setCourtID(rs.getInt("CourtID"));
        court.setCourtTypeID(rs.getInt("CourtTypeID"));
        court.setCourtName(rs.getString("CourtName"));
        court.setLocation(rs.getString("Location"));
        court.setPricePerHour(rs.getBigDecimal("PricePerHour"));
        court.setDescription(rs.getString("Description"));
        court.setImageURL(rs.getString("ImageURL"));
        court.setStatus(rs.getString("Status"));
        court.setActive(rs.getBoolean("IsActive"));
        Timestamp createdAt = rs.getTimestamp("CreatedAt");
        if (createdAt != null) court.setCreatedAt(createdAt.toLocalDateTime());
        court.setTypeName(rs.getString("TypeName"));
        court.setTypeSlug(rs.getString("TypeSlug"));
        return court;
    }

    public static final class CourtUpdateOutcome {
        private final boolean found;
        private final boolean updated;
        private final int blockingCount;

        public CourtUpdateOutcome(boolean found, boolean updated,
                int blockingCount) {
            this.found = found;
            this.updated = updated;
            this.blockingCount = blockingCount;
        }
        public boolean isFound() { return found; }
        public boolean isUpdated() { return updated; }
        public int getBlockingCount() { return blockingCount; }
    }
}
