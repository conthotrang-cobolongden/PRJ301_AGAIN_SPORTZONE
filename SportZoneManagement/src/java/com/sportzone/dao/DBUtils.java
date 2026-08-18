package com.sportzone.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Tao ket noi SQL Server tu cau hinh classpath cua ung dung.
 */
public final class DBUtils {

    private static final String DRIVER_CLASS = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String CONFIG_RESOURCE = "db.properties";
    private static final String EXAMPLE_RESOURCE = "db.properties.example";
    private static final Properties CONFIG = loadConfiguration();

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
        return DriverManager.getConnection(
                resolve("db.url", "SPORTZONE_DB_URL"),
                resolve("db.username", "SPORTZONE_DB_USERNAME"),
                resolve("db.password", "SPORTZONE_DB_PASSWORD")
        );
    }

    private static Properties loadConfiguration() {
        Properties properties = new Properties();

        InputStream configuredInput = DBUtils.class.getResourceAsStream(CONFIG_RESOURCE);
        InputStream selectedInput = configuredInput != null ? configuredInput
                : DBUtils.class.getResourceAsStream(EXAMPLE_RESOURCE);
        try (InputStream input = selectedInput) {
            if (input == null) throw new IllegalStateException("Missing database configuration template");
            properties.load(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot read database configuration", exception);
        }

        requireProperty(properties, "db.url");
        requireProperty(properties, "db.username");
        return properties;
    }

    private static String resolve(String key, String environmentKey) {
        String systemValue = System.getProperty("sportzone." + key);
        if (systemValue != null && !systemValue.trim().isEmpty()) return systemValue;
        String environmentValue = System.getenv(environmentKey);
        if (environmentValue != null && !environmentValue.trim().isEmpty()) return environmentValue;
        return CONFIG.getProperty(key, "");
    }

    private static void requireProperty(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException("Missing database property: " + key);
        }
    }
}
