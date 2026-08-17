package com.sportzone.dao;

import com.sportzone.model.Account;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AccountDAO {

    private static final String SELECT_WITH_ROLE =
            "SELECT a.AccountID, a.RoleID, r.RoleName, a.Email, a.Phone, a.PasswordHash, "
            + "a.FullName, a.Status, a.LastLoginAt, a.CreatedAt "
            + "FROM Accounts a JOIN Roles r ON a.RoleID = r.RoleID ";

    public Account findByEmail(String email) throws SQLException {
        String sql = SELECT_WITH_ROLE + "WHERE a.Email = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DBUtils.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, email);
            resultSet = statement.executeQuery();
            return resultSet.next() ? mapAccount(resultSet) : null;
        } finally {
            DBUtils.closeQuietly(resultSet);
            DBUtils.closeQuietly(statement);
            DBUtils.closeQuietly(connection);
        }
    }

    public Account findById(int accountId) throws SQLException {
        String sql = SELECT_WITH_ROLE + "WHERE a.AccountID = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DBUtils.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, accountId);
            resultSet = statement.executeQuery();
            return resultSet.next() ? mapAccount(resultSet) : null;
        } finally {
            DBUtils.closeQuietly(resultSet);
            DBUtils.closeQuietly(statement);
            DBUtils.closeQuietly(connection);
        }
    }

    public boolean existsByEmail(String email) throws SQLException {
        String sql = "SELECT 1 FROM Accounts WHERE Email = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DBUtils.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, email);
            resultSet = statement.executeQuery();
            return resultSet.next();
        } finally {
            DBUtils.closeQuietly(resultSet);
            DBUtils.closeQuietly(statement);
            DBUtils.closeQuietly(connection);
        }
    }

    public boolean existsByPhone(String phone) throws SQLException {
        String sql = "SELECT 1 FROM Accounts WHERE Phone = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DBUtils.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, phone);
            resultSet = statement.executeQuery();
            return resultSet.next();
        } finally {
            DBUtils.closeQuietly(resultSet);
            DBUtils.closeQuietly(statement);
            DBUtils.closeQuietly(connection);
        }
    }

    public int insert(Account account) throws SQLException {
        String sql = "INSERT INTO Accounts (RoleID, Email, Phone, PasswordHash, FullName, Status) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;
        try {
            connection = DBUtils.getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, account.getRoleId());
            statement.setString(2, account.getEmail());
            statement.setString(3, account.getPhone());
            statement.setString(4, account.getPasswordHash());
            statement.setString(5, account.getFullName());
            statement.setString(6, account.getStatus());
            statement.executeUpdate();
            generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
            throw new SQLException("Insert into Accounts failed: no generated key returned");
        } finally {
            DBUtils.closeQuietly(generatedKeys);
            DBUtils.closeQuietly(statement);
            DBUtils.closeQuietly(connection);
        }
    }

    public void updateLastLogin(int accountId) throws SQLException {
        String sql = "UPDATE Accounts SET LastLoginAt = GETDATE() WHERE AccountID = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DBUtils.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, accountId);
            statement.executeUpdate();
        } finally {
            DBUtils.closeQuietly(statement);
            DBUtils.closeQuietly(connection);
        }
    }

    /** U5: cập nhật hồ sơ (họ tên, email, SĐT). UNIQUE Email/Phone chặn trùng — servlet catch 23000. */
    public void updateProfile(int accountId, String fullName, String email, String phone) throws SQLException {
        String sql = "UPDATE Accounts SET FullName = ?, Email = ?, Phone = ? WHERE AccountID = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DBUtils.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, fullName);
            statement.setString(2, email);
            statement.setString(3, phone);
            statement.setInt(4, accountId);
            statement.executeUpdate();
        } finally {
            DBUtils.closeQuietly(statement);
            DBUtils.closeQuietly(connection);
        }
    }

    /** U6: đổi mật khẩu (hash SHA-256 đã tính ở service). */
    public void updatePasswordHash(int accountId, String passwordHash) throws SQLException {
        String sql = "UPDATE Accounts SET PasswordHash = ? WHERE AccountID = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DBUtils.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, passwordHash);
            statement.setInt(2, accountId);
            statement.executeUpdate();
        } finally {
            DBUtils.closeQuietly(statement);
            DBUtils.closeQuietly(connection);
        }
    }

    /** U5: kiểm tra email đã bị tài khoản KHÁC dùng chưa (loại trừ chính mình). */
    public boolean existsByEmailExcluding(String email, int excludeAccountId) throws SQLException {
        String sql = "SELECT 1 FROM Accounts WHERE Email = ? AND AccountID <> ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DBUtils.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, email);
            statement.setInt(2, excludeAccountId);
            resultSet = statement.executeQuery();
            return resultSet.next();
        } finally {
            DBUtils.closeQuietly(resultSet);
            DBUtils.closeQuietly(statement);
            DBUtils.closeQuietly(connection);
        }
    }

    /** U5: kiểm tra SĐT đã bị tài khoản KHÁC dùng chưa (loại trừ chính mình). */
    public boolean existsByPhoneExcluding(String phone, int excludeAccountId) throws SQLException {
        String sql = "SELECT 1 FROM Accounts WHERE Phone = ? AND AccountID <> ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DBUtils.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, phone);
            statement.setInt(2, excludeAccountId);
            resultSet = statement.executeQuery();
            return resultSet.next();
        } finally {
            DBUtils.closeQuietly(resultSet);
            DBUtils.closeQuietly(statement);
            DBUtils.closeQuietly(connection);
        }
    }

    private Account mapAccount(ResultSet resultSet) throws SQLException {
        Account account = new Account();
        account.setAccountId(resultSet.getInt("AccountID"));
        account.setRoleId(resultSet.getInt("RoleID"));
        account.setRoleName(resultSet.getString("RoleName"));
        account.setEmail(resultSet.getString("Email"));
        account.setPhone(resultSet.getString("Phone"));
        account.setPasswordHash(resultSet.getString("PasswordHash"));
        account.setFullName(resultSet.getString("FullName"));
        account.setStatus(resultSet.getString("Status"));
        account.setLastLoginAt(resultSet.getTimestamp("LastLoginAt"));
        account.setCreatedAt(resultSet.getTimestamp("CreatedAt"));
        return account;
    }
}
