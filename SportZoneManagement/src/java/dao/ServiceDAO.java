package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.Service;

/**
 * Truy van dich vu phu cho booking.
 */
public class ServiceDAO {

    private static final String SERVICE_SELECT
            = "SELECT ServiceID, ServiceName, Description, Price, "
            + "DurationMinutes, IsActive, CreatedAt "
            + "FROM Services ";

    public List<Service> findAllActive() throws SQLException {
        String sql = SERVICE_SELECT
                + "WHERE IsActive = 1 "
                + "ORDER BY ServiceName";

        List<Service> services = new ArrayList<>();

        try (Connection connection = DBUtils.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                services.add(mapService(resultSet));
            }
        }

        return services;
    }

    /**
     * Dung connection cua BookingService de cung transaction.
     */
    public Service findActiveByID(
            Connection connection,
            int serviceID
    ) throws SQLException {
        String sql = SERVICE_SELECT
                + "WHERE ServiceID = ? "
                + "AND IsActive = 1";

        try (PreparedStatement statement
                = connection.prepareStatement(sql)) {

            statement.setInt(1, serviceID);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next()
                        ? mapService(resultSet)
                        : null;
            }
        }
    }

    private Service mapService(ResultSet resultSet)
            throws SQLException {
        Service service = new Service();

        service.setServiceID(
                resultSet.getInt("ServiceID")
        );
        service.setServiceName(
                resultSet.getString("ServiceName")
        );
        service.setDescription(
                resultSet.getString("Description")
        );
        service.setPrice(
                resultSet.getBigDecimal("Price")
        );
        service.setDurationMinutes(
                resultSet.getInt("DurationMinutes")
        );
        service.setActive(
                resultSet.getBoolean("IsActive")
        );

        Timestamp createdAt
                = resultSet.getTimestamp("CreatedAt");

        if (createdAt != null) {
            service.setCreatedAt(
                    createdAt.toLocalDateTime()
            );
        }

        return service;
    }
}