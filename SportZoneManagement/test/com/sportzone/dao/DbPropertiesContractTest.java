package com.sportzone.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;

/**
 * Kiem tra contract cau hinh ket noi SportZoneDB cua Phase 1.
 */
public final class DbPropertiesContractTest {

    private static final String RESOURCE_PATH = "/com/sportzone/dao/db.properties";

    private DbPropertiesContractTest() {
    }

    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();

        try (InputStream input = DbPropertiesContractTest.class.getResourceAsStream(RESOURCE_PATH)) {
            require(input != null, "Missing classpath resource: " + RESOURCE_PATH);
            properties.load(input);
        }

        requirePresent(properties, "db.url");
        requirePresent(properties, "db.username");
        requirePresent(properties, "db.password");
        require(properties.getProperty("db.url").contains("databaseName=SportZoneDB"),
                "db.url must target SportZoneDB");

        try (Connection connection = DBUtils.getConnection()) {
            require(connection != null && !connection.isClosed(), "DB connection must be open");
            require("SportZoneDB".equalsIgnoreCase(connection.getCatalog()),
                    "DB connection must use SportZoneDB");
        }

        System.out.println("DbPropertiesContractTest PASSED");
    }

    private static void requirePresent(Properties properties, String key) {
        String value = properties.getProperty(key);
        require(value != null && !value.trim().isEmpty(), "Missing property: " + key);
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
