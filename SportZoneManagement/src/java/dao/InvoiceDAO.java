package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;

/**
 * Truy van hoa don phuc vu membership va Phase 6.
 */
public class InvoiceDAO {

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