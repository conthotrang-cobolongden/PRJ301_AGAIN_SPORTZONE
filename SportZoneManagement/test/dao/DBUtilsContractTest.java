package dao;

import java.sql.Connection;

/**
 * Kiem tra contract ket noi SportZoneDB cua Phase 1.
 */
public final class DBUtilsContractTest {

    private DBUtilsContractTest() {
    }

    public static void main(String[] args) throws Exception {
        try (Connection connection = DBUtils.getConnection()) {
            require(connection != null && !connection.isClosed(), "DB connection must be open");
            require("SportZoneDB".equalsIgnoreCase(connection.getCatalog()),
                    "DB connection must use SportZoneDB");
        }

        System.out.println("DBUtilsContractTest PASSED");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
