package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import model.Account;

/**
 * Persistence operations for accounts used by authentication.
 */
public class AccountDAO {

    private static final String ACCOUNT_SELECT
            = "SELECT a.AccountID, a.RoleID, r.RoleName, a.Email, a.Phone, "
            + "a.PasswordHash, a.FullName, a.Status, a.LastLoginAt, a.CreatedAt "
            + "FROM Accounts a JOIN Roles r ON a.RoleID = r.RoleID ";

    public Account findByEmail(String email) throws SQLException {
        String sql = ACCOUNT_SELECT + "WHERE a.Email = ?";
        try (Connection connection = DBUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapAccount(resultSet) : null;
            }
        }
    }

    public Account findById(int accountID) throws SQLException {
        String sql = ACCOUNT_SELECT + "WHERE a.AccountID = ?";
        try (Connection connection = DBUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, accountID);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapAccount(resultSet) : null;
            }
        }
    }

    public boolean existsByEmail(String email) throws SQLException {
        String sql = "SELECT 1 FROM Accounts WHERE Email = ?";
        try (Connection connection = DBUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public boolean existsByPhone(String phone) throws SQLException {
        String sql = "SELECT 1 FROM Accounts WHERE Phone = ?";
        try (Connection connection = DBUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, phone);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public int insert(Account account) throws SQLException {
        String sql = "INSERT INTO Accounts "
                + "(RoleID, Email, Phone, PasswordHash, FullName, Status) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBUtils.getConnection()) {
            connection.setAutoCommit(false);
            try {
                int accountID;
                try (PreparedStatement statement = connection.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS)) {
                    statement.setInt(1, account.getRoleID());
                    statement.setString(2, account.getEmail());
                    statement.setString(3, account.getPhone());
                    statement.setString(4, account.getPasswordHash());
                    statement.setString(5, account.getFullName());
                    statement.setString(6, account.getStatus());
                    statement.executeUpdate();

                    try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                        if (!generatedKeys.next()) {
                            throw new SQLException(
                                    "Inserting account failed: no generated key returned");
                        }
                        accountID = generatedKeys.getInt(1);
                    }
                }
                connection.commit();
                return accountID;
            } catch (SQLException | RuntimeException | Error exception) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    exception.addSuppressed(rollbackException);
                }
                throw exception;
            }
        }
    }

    public void updateLastLogin(int accountID) throws SQLException {
        String sql = "UPDATE Accounts SET LastLoginAt = GETDATE() WHERE AccountID = ?";
        try (Connection connection = DBUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, accountID);
            statement.executeUpdate();
        }
    }

    private Account mapAccount(ResultSet resultSet) throws SQLException {
        Account account = new Account();
        account.setAccountID(resultSet.getInt("AccountID"));
        account.setRoleID(resultSet.getInt("RoleID"));
        account.setRoleName(resultSet.getString("RoleName"));
        account.setEmail(resultSet.getString("Email"));
        account.setPhone(resultSet.getString("Phone"));
        account.setPasswordHash(resultSet.getString("PasswordHash"));
        account.setFullName(resultSet.getString("FullName"));
        account.setStatus(resultSet.getString("Status"));

        Timestamp lastLoginAt = resultSet.getTimestamp("LastLoginAt");
        if (lastLoginAt != null) {
            account.setLastLoginAt(lastLoginAt.toLocalDateTime());
        }
        Timestamp createdAt = resultSet.getTimestamp("CreatedAt");
        if (createdAt != null) {
            account.setCreatedAt(createdAt.toLocalDateTime());
        }
        return account;
    }
}
