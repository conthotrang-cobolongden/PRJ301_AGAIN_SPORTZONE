package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Tao ket noi truc tiep toi SportZoneDB.
 */
public final class DBUtils {

    private static final String DRIVER_CLASS = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String URL = "jdbc:sqlserver://localhost:1433;"
            + "databaseName=SportZoneDB;encrypt=true;trustServerCertificate=true";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "12345";

    static {
        try {
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private DBUtils() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
