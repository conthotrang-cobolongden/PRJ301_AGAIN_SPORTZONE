package com.sportzone.util;

import com.sportzone.dao.DBUtils;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public final class HealthCheck {

    public static final String SAMPLE_PASSWORD = "Admin@123";
    public static final String EXPECTED_ADMIN_HASH =
            "e86f78a8a3caf0b60d8e74e5942aa6d86dc150cd3c03338aef25b7d2d7e3acc7";

    private HealthCheck() {
    }

    public static String passwordHash() {
        return PasswordUtil.sha256Hex(SAMPLE_PASSWORD);
    }

    public static boolean passwordHashMatches() {
        return EXPECTED_ADMIN_HASH.equalsIgnoreCase(passwordHash());
    }

    public static String pingDatabase() {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DBUtils.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(
                    "SELECT DB_NAME() AS dbName, (SELECT COUNT(*) FROM sys.tables) AS tableCount");
            if (resultSet.next()) {
                return "OK db=" + resultSet.getString("dbName")
                        + " tables=" + resultSet.getInt("tableCount");
            }
            return "OK";
        } catch (Exception ex) {
            return "FAIL " + ex.getMessage();
        } finally {
            DBUtils.closeQuietly(resultSet);
            DBUtils.closeQuietly(statement);
            DBUtils.closeQuietly(connection);
        }
    }

    public static void main(String[] args) {
        System.out.println("SHA-256(" + SAMPLE_PASSWORD + ") = " + passwordHash());
        System.out.println("Hash matches seed             = " + passwordHashMatches());
        System.out.println("Database                      = " + pingDatabase());
    }
}
