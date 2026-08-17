package com.sportzone.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DBUtils {

    private static final String PROPERTIES_FILE = "/db.properties";
    private static final Properties SETTINGS = new Properties();

    static {
        InputStream in = DBUtils.class.getResourceAsStream(PROPERTIES_FILE);
        if (in == null) {
            throw new ExceptionInInitializerError("Missing classpath file: db.properties");
        }
        try {
            SETTINGS.load(in);
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (IOException | ClassNotFoundException ex) {
            throw new ExceptionInInitializerError(ex);
        } finally {
            try {
                in.close();
            } catch (IOException ignored) {
            }
        }
    }

    private DBUtils() {
    }

    public static Connection getConnection() throws SQLException {
        String url = required("db.url");
        boolean integrated = Boolean.parseBoolean(SETTINGS.getProperty("db.integratedSecurity", "false"));
        if (integrated) {
            if (!url.toLowerCase().contains("integratedsecurity")) {
                url = appendParam(url, "integratedSecurity=true");
            }
            return DriverManager.getConnection(url);
        }
        String username = SETTINGS.getProperty("db.username", "").trim();
        String password = SETTINGS.getProperty("db.password", "");
        if (username.isEmpty()) {
            throw new SQLException("db.username is empty and integratedSecurity is false");
        }
        return DriverManager.getConnection(url, username, password);
    }

    public static void closeQuietly(AutoCloseable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception ignored) {
        }
    }

    private static String required(String key) throws SQLException {
        String value = SETTINGS.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new SQLException("Missing property: " + key);
        }
        return value.trim();
    }

    private static String appendParam(String url, String param) {
        if (url.endsWith(";")) {
            return url + param;
        }
        return url + ";" + param;
    }
}
