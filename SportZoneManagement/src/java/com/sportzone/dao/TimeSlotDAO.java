package com.sportzone.dao;

import com.sportzone.model.TimeSlot;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho bảng TimeSlots (U9; P5 đặt/hủy; A11–A14 quản lý khung giờ).
 */
public class TimeSlotDAO {

    private static final String SELECT_BASE =
            "SELECT TimeSlotID, CourtID, SlotDate, StartTime, EndTime, Status, CreatedAt "
            + "FROM TimeSlots ";

    /** U9: khung giờ của 1 sân trong 1 ngày, xếp theo giờ bắt đầu. */
    public List<TimeSlot> listByCourtAndDate(int courtId, Date slotDate) throws SQLException {
        String sql = SELECT_BASE + "WHERE CourtID = ? AND SlotDate = ? ORDER BY StartTime";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DBUtils.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, courtId);
            statement.setDate(2, slotDate);
            resultSet = statement.executeQuery();
            List<TimeSlot> slots = new ArrayList<>();
            while (resultSet.next()) {
                slots.add(mapSlot(resultSet));
            }
            return slots;
        } finally {
            DBUtils.closeQuietly(resultSet);
            DBUtils.closeQuietly(statement);
            DBUtils.closeQuietly(connection);
        }
    }

    /** P5: lấy slot theo id để validate khi đặt sân (Rule 6: CourtID phải khớp). */
    public TimeSlot findById(int timeSlotId) throws SQLException {
        String sql = SELECT_BASE + "WHERE TimeSlotID = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DBUtils.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, timeSlotId);
            resultSet = statement.executeQuery();
            return resultSet.next() ? mapSlot(resultSet) : null;
        } finally {
            DBUtils.closeQuietly(resultSet);
            DBUtils.closeQuietly(statement);
            DBUtils.closeQuietly(connection);
        }
    }

    /** P5: đổi trạng thái slot (Available <-> Booked, Closed). Gọi trong transaction của service. */
    public void updateStatus(Connection connection, int timeSlotId, String status) throws SQLException {
        String sql = "UPDATE TimeSlots SET Status = ? WHERE TimeSlotID = ?";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1, status);
            statement.setInt(2, timeSlotId);
            statement.executeUpdate();
        } finally {
            DBUtils.closeQuietly(statement);
        }
    }

    /** A11: khung giờ của 1 ngày (admin xem theo ngày, mọi sân). */
    public List<TimeSlot> listByDate(Date slotDate) throws SQLException {
        String sql = SELECT_BASE + "WHERE SlotDate = ? ORDER BY CourtID, StartTime";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DBUtils.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setDate(1, slotDate);
            resultSet = statement.executeQuery();
            List<TimeSlot> slots = new ArrayList<>();
            while (resultSet.next()) {
                slots.add(mapSlot(resultSet));
            }
            return slots;
        } finally {
            DBUtils.closeQuietly(resultSet);
            DBUtils.closeQuietly(statement);
            DBUtils.closeQuietly(connection);
        }
    }

    /** A12: tạo khung giờ (admin). UNIQUE (CourtID, SlotDate, StartTime) chặn trùng. */
    public int insert(TimeSlot slot) throws SQLException {
        String sql = "INSERT INTO TimeSlots (CourtID, SlotDate, StartTime, EndTime, Status) "
                + "VALUES (?, ?, ?, ?, ?)";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;
        try {
            connection = DBUtils.getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, slot.getCourtId());
            statement.setDate(2, slot.getSlotDate());
            statement.setTime(3, slot.getStartTime());
            statement.setTime(4, slot.getEndTime());
            statement.setString(5, slot.getStatus());
            statement.executeUpdate();
            generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
            throw new SQLException("Insert into TimeSlots failed: no generated key returned");
        } finally {
            DBUtils.closeQuietly(generatedKeys);
            DBUtils.closeQuietly(statement);
            DBUtils.closeQuietly(connection);
        }
    }

    /** A13: sửa giờ bắt đầu/kết thúc (chỉ khi slot chưa Booked — service kiểm tra). */
    public void updateTimes(int timeSlotId, java.sql.Time startTime, java.sql.Time endTime) throws SQLException {
        String sql = "UPDATE TimeSlots SET StartTime = ?, EndTime = ? WHERE TimeSlotID = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DBUtils.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setTime(1, startTime);
            statement.setTime(2, endTime);
            statement.setInt(3, timeSlotId);
            statement.executeUpdate();
        } finally {
            DBUtils.closeQuietly(statement);
            DBUtils.closeQuietly(connection);
        }
    }

    /** A14: đóng khung giờ (soft — chuyển Closed, không xóa dòng đã Booked). */
    public void close(int timeSlotId) throws SQLException {
        String sql = "UPDATE TimeSlots SET Status = 'Closed' WHERE TimeSlotID = ? AND Status <> 'Booked'";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DBUtils.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, timeSlotId);
            statement.executeUpdate();
        } finally {
            DBUtils.closeQuietly(statement);
            DBUtils.closeQuietly(connection);
        }
    }

    private TimeSlot mapSlot(ResultSet resultSet) throws SQLException {
        TimeSlot slot = new TimeSlot();
        slot.setTimeSlotId(resultSet.getInt("TimeSlotID"));
        slot.setCourtId(resultSet.getInt("CourtID"));
        slot.setSlotDate(resultSet.getDate("SlotDate"));
        slot.setStartTime(resultSet.getTime("StartTime"));
        slot.setEndTime(resultSet.getTime("EndTime"));
        slot.setStatus(resultSet.getString("Status"));
        slot.setCreatedAt(resultSet.getTimestamp("CreatedAt"));
        return slot;
    }
}
