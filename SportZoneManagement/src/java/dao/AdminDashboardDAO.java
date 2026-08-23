package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.AdminDashboard;

public class AdminDashboardDAO {

    public AdminDashboard load() throws SQLException {
        String sql = "SELECT "
                + "(SELECT COUNT(*) FROM Accounts) AS TotalAccounts, "
                + "(SELECT COUNT(*) FROM Courts WHERE IsActive=1) AS TotalCourts, "
                + "(SELECT COUNT(*) FROM Bookings) AS TotalBookings, "
                + "(SELECT COALESCE(SUM(FinalAmount),0) FROM Invoices "
                + " WHERE PaymentStatus=N'Paid') AS TotalRevenue";
        try (Connection connection = DBUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            if (!resultSet.next()) {
                throw new SQLException("Admin dashboard query returned no row");
            }
            return new AdminDashboard(resultSet.getInt("TotalAccounts"),
                    resultSet.getInt("TotalCourts"),
                    resultSet.getInt("TotalBookings"),
                    resultSet.getBigDecimal("TotalRevenue"));
        }
    }
}
