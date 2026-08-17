package com.sportzone.dao;

import com.sportzone.model.Court;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho bảng Courts (U7/U8; A8–A10 dùng chung các query đọc).
 * Mọi query đọc đều JOIN CourtTypes để lấy typeName/typeSlug cho view.
 */
public class CourtDAO {

    private static final String SELECT_WITH_TYPE =
            "SELECT c.CourtID, c.CourtTypeID, ct.TypeName, c.CourtName, c.Location, "
            + "c.PricePerHour, c.Description, c.ImageURL, c.Status, c.IsActive, c.CreatedAt "
            + "FROM Courts c JOIN CourtTypes ct ON c.CourtTypeID = ct.CourtTypeID ";

    /** U7: danh sách sân đang hoạt động, lọc theo loại (all/tennis/pickleball). */
    public List<Court> list(String typeFilter) throws SQLException {
        StringBuilder sql = new StringBuilder(SELECT_WITH_TYPE).append("WHERE c.IsActive = 1");
        boolean byType = "tennis".equalsIgnoreCase(typeFilter) || "pickleball".equalsIgnoreCase(typeFilter);
        if (byType) {
            sql.append(" AND ct.TypeName = ?");
        }
        sql.append(" ORDER BY c.CourtTypeID, c.CourtName");

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DBUtils.getConnection();
            statement = connection.prepareStatement(sql.toString());
            if (byType) {
                statement.setString(1, "tennis".equalsIgnoreCase(typeFilter) ? "Tennis" : "Pickleball");
            }
            resultSet = statement.executeQuery();
            List<Court> courts = new ArrayList<>();
            while (resultSet.next()) {
                courts.add(mapCourt(resultSet));
            }
            return courts;
        } finally {
            DBUtils.closeQuietly(resultSet);
            DBUtils.closeQuietly(statement);
            DBUtils.closeQuietly(connection);
        }
    }

    /** U7: đếm số sân theo loại để hiện badge trên tab lọc. */
    public int countByType(String typeName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Courts c JOIN CourtTypes ct ON c.CourtTypeID = ct.CourtTypeID "
                + "WHERE c.IsActive = 1 AND ct.TypeName = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DBUtils.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, typeName);
            resultSet = statement.executeQuery();
            return resultSet.next() ? resultSet.getInt(1) : 0;
        } finally {
            DBUtils.closeQuietly(resultSet);
            DBUtils.closeQuietly(statement);
            DBUtils.closeQuietly(connection);
        }
    }

    /** U8: chi tiết 1 sân (kèm loại sân). Trả null nếu không tồn tại hoặc đã ẩn. */
    public Court findById(int courtId) throws SQLException {
        String sql = SELECT_WITH_TYPE + "WHERE c.CourtID = ? AND c.IsActive = 1";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DBUtils.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, courtId);
            resultSet = statement.executeQuery();
            return resultSet.next() ? mapCourt(resultSet) : null;
        } finally {
            DBUtils.closeQuietly(resultSet);
            DBUtils.closeQuietly(statement);
            DBUtils.closeQuietly(connection);
        }
    }

    /** U10 (P5): chỉ sân Available để đưa vào form đặt sân. */
    public List<Court> listBookable() throws SQLException {
        String sql = SELECT_WITH_TYPE
                + "WHERE c.IsActive = 1 AND c.Status = 'Available' ORDER BY c.CourtTypeID, c.CourtName";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DBUtils.getConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            List<Court> courts = new ArrayList<>();
            while (resultSet.next()) {
                courts.add(mapCourt(resultSet));
            }
            return courts;
        } finally {
            DBUtils.closeQuietly(resultSet);
            DBUtils.closeQuietly(statement);
            DBUtils.closeQuietly(connection);
        }
    }

    private Court mapCourt(ResultSet resultSet) throws SQLException {
        Court court = new Court();
        court.setCourtId(resultSet.getInt("CourtID"));
        court.setCourtTypeId(resultSet.getInt("CourtTypeID"));
        String typeName = resultSet.getString("TypeName");
        court.setTypeName(typeName);
        court.setTypeSlug(typeName == null ? null : typeName.toLowerCase());
        court.setCourtName(resultSet.getString("CourtName"));
        court.setLocation(resultSet.getString("Location"));
        court.setPricePerHour(resultSet.getBigDecimal("PricePerHour"));
        court.setDescription(resultSet.getString("Description"));
        court.setImageUrl(resultSet.getString("ImageURL"));
        court.setStatus(resultSet.getString("Status"));
        court.setActive(resultSet.getBoolean("IsActive"));
        court.setCreatedAt(resultSet.getTimestamp("CreatedAt"));
        return court;
    }
}
