package utils;

import dao.DBUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Kiem tra nhanh database va contract hash cua Phase 1. */
public final class HealthCheck {

    private static final String ADMIN_HASH =
            "e86f78a8a3caf0b60d8e74e5942aa6d86dc150cd3c03338aef25b7d2d7e3acc7";

    private HealthCheck() { }

    public static Result run() throws SQLException {
        String sql = "SELECT COUNT(*) AS TableCount FROM INFORMATION_SCHEMA.TABLES"
                + " WHERE TABLE_TYPE = 'BASE TABLE'";
        int tableCount;

        try (Connection connection = DBUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            tableCount = resultSet.getInt("TableCount");
        }

        return new Result(true, tableCount,
                ADMIN_HASH.equals(PasswordUtil.sha256Hex("Admin@123")));
    }

    public static final class Result {
        private final boolean databaseOk;
        private final int tableCount;
        private final boolean passwordHashOk;

        public Result(boolean databaseOk, int tableCount, boolean passwordHashOk) {
            this.databaseOk = databaseOk;
            this.tableCount = tableCount;
            this.passwordHashOk = passwordHashOk;
        }
        public boolean isDatabaseOk() { return databaseOk; }
        public int getTableCount() { return tableCount; }
        public boolean isPasswordHashOk() { return passwordHashOk; }
        public boolean isHealthy() { return databaseOk && tableCount == 9 && passwordHashOk; }
    }
}
