package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import model.Court;

/**
 * Truy van danh sach va chi tiet san cho khu vuc public.
 *
 * Rule Phase 3:
 * - Chi tra ve Courts.IsActive = 1.
 * - Chi tra ve san thuoc CourtTypes.IsActive = 1.
 * - Maintenance va Unavailable van duoc xem chi tiet.
 * - JOIN CourtTypes de cung cap typeName va typeSlug cho JSP.
 *
 * Tag: U7, U8, U19
 */
public class CourtDAO {

    /**
     * Cac cot dung chung khi map Court.
     *
     * Khong dung SELECT * de tranh trung ten cot CourtID, IsActive
     * va tranh mapper bi anh huong khi schema duoc bo sung cot.
     */
    private static final String COURT_SELECT
            = "SELECT "
            + "c.CourtID, "
            + "c.CourtTypeID, "
            + "c.CourtName, "
            + "c.Location, "
            + "c.PricePerHour, "
            + "c.Description, "
            + "c.ImageURL, "
            + "c.Status, "
            + "c.IsActive, "
            + "c.CreatedAt, "
            + "ct.TypeName, "
            + "LOWER(ct.TypeName) AS TypeSlug "
            + "FROM Courts c "
            + "INNER JOIN CourtTypes ct "
            + "ON c.CourtTypeID = ct.CourtTypeID ";

    /**
     * Lay danh sach san public theo loai san.
     *
     * typeFilter:
     * - null, rong hoac "all": lay tat ca san active.
     * - "tennis": chi lay san Tennis.
     * - "pickleball": chi lay san Pickleball.
     * - Gia tri khac: tra ve danh sach rong.
     *
     * @param typeFilter slug loai san
     * @return danh sach san sap xep tang dan theo CourtID
     * @throws SQLException neu truy van database that bai
     *
     * Tag: U7
     */
    public List<Court> findAll(String typeFilter)
            throws SQLException {
        String normalizedType = normalizeTypeFilter(typeFilter);

        StringBuilder sql = new StringBuilder(COURT_SELECT);
        sql.append("WHERE c.IsActive = 1 ");
        sql.append("AND ct.IsActive = 1 ");

        if (normalizedType != null) {
            sql.append("AND LOWER(ct.TypeName) = ? ");
        }

        sql.append("ORDER BY c.CourtID");

        List<Court> courts = new ArrayList<>();

        try (Connection connection = DBUtils.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(sql.toString())) {
            if (normalizedType != null) {
                statement.setString(1, normalizedType);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    courts.add(mapCourt(resultSet));
                }
            }
        }

        return courts;
    }

    /**
     * Lay mot san public theo ID.
     *
     * Sân Maintenance hoac Unavailable van duoc tra ve neu IsActive = 1.
     * JSP se an nut dat san dua tren Court.status.
     *
     * @param courtID ID san can xem
     * @return Court neu ton tai va active; null neu khong tim thay
     * @throws SQLException neu truy van database that bai
     *
     * Tag: U8
     */
    public Court findById(int courtID)
            throws SQLException {
        String sql
                = COURT_SELECT
                + "WHERE c.CourtID = ? "
                + "AND c.IsActive = 1 "
                + "AND ct.IsActive = 1";

        try (Connection connection = DBUtils.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(sql)) {
            statement.setInt(1, courtID);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next()
                        ? mapCourt(resultSet)
                        : null;
            }
        }
    }

    /**
     * Dem so san active theo loai.
     *
     * Method nay dung cho:
     * - So luong tren filter pill cua /courts.
     * - So luong san Tennis/Pickleball tren /home.
     *
     * @param typeFilter tennis, pickleball, all hoac null
     * @return so san active thoa man filter
     * @throws SQLException neu truy van database that bai
     *
     * Tag: U7, U19
     */
    public int countByType(String typeFilter)
            throws SQLException {
        String normalizedType = normalizeTypeFilter(typeFilter);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) AS CourtCount ");
        sql.append("FROM Courts c ");
        sql.append("INNER JOIN CourtTypes ct ");
        sql.append("ON c.CourtTypeID = ct.CourtTypeID ");
        sql.append("WHERE c.IsActive = 1 ");
        sql.append("AND ct.IsActive = 1 ");

        if (normalizedType != null) {
            sql.append("AND LOWER(ct.TypeName) = ?");
        }

        try (Connection connection = DBUtils.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(sql.toString())) {
            if (normalizedType != null) {
                statement.setString(1, normalizedType);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new SQLException(
                            "Court count query returned no result"
                    );
                }

                return resultSet.getInt("CourtCount");
            }
        }
    }

    /**
     * Chuan hoa filter ma khong chen truc tiep input vao SQL.
     *
     * null duoc dung de bieu dien "khong ap dung filter".
     * Moi filter khac duoc bind bang PreparedStatement.
     */
    private String normalizeTypeFilter(String typeFilter) {
        if (typeFilter == null) {
            return null;
        }

        String normalized
                = typeFilter.trim().toLowerCase(Locale.ROOT);

        if (normalized.isEmpty()
                || "all".equals(normalized)) {
            return null;
        }

        return normalized;
    }

    /**
     * Map mot dong JOIN Courts + CourtTypes thanh Court.
     *
     * typeName va typeSlug la view fields, khong phai cot truc tiep
     * cua entity Courts.
     */
    private Court mapCourt(ResultSet resultSet)
            throws SQLException {
        Court court = new Court();

        court.setCourtID(
                resultSet.getInt("CourtID")
        );

        court.setCourtTypeID(
                resultSet.getInt("CourtTypeID")
        );

        court.setCourtName(
                resultSet.getString("CourtName")
        );

        court.setLocation(
                resultSet.getString("Location")
        );

        court.setPricePerHour(
                resultSet.getBigDecimal("PricePerHour")
        );

        court.setDescription(
                resultSet.getString("Description")
        );

        court.setImageURL(
                resultSet.getString("ImageURL")
        );

        court.setStatus(
                resultSet.getString("Status")
        );

        court.setActive(
                resultSet.getBoolean("IsActive")
        );

        Timestamp createdAt
                = resultSet.getTimestamp("CreatedAt");

        if (createdAt != null) {
            court.setCreatedAt(
                    createdAt.toLocalDateTime()
            );
        }

        court.setTypeName(
                resultSet.getString("TypeName")
        );

        court.setTypeSlug(
                resultSet.getString("TypeSlug")
        );

        return court;
    }
}