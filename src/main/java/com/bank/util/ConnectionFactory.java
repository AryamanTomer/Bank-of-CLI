package com.bank.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class ConnectionFactory {
    private static final Properties LOCAL = loadLocalProperties();

    private ConnectionFactory() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url(), username(), password());
    }

    public static boolean isConfigured() {
        return notBlank(username());
    }

    static String url() {
        if (Boolean.parseBoolean(System.getProperty("bank.test.db", "false"))) {
            return firstNonBlank(envOrLocal("TEST_DB_URL"), "jdbc:postgresql://localhost:5432/bank_cli_test");
        }
        return firstNonBlank(envOrLocal("DB_URL"), "jdbc:postgresql://localhost:5432/bank_cli");
    }

    private static String username() {
        return firstNonBlank(envOrLocal("DB_USERNAME"), envOrLocal("DB_USER"), "postgres");
    }

    private static String password() {
        String value = envOrLocal("DB_PASSWORD");
        return value == null ? "" : value;
    }

    private static String envOrLocal(String key) {
        String env = System.getenv(key);
        if (notBlank(env)) {
            return env;
        }
        String local = LOCAL.getProperty(key);
        return notBlank(local) ? local : null;
    }

    private static Properties loadLocalProperties() {
        Properties properties = new Properties();
        Path path = Path.of("db.properties");
        if (!Files.exists(path)) {
            return properties;
        }
        try (InputStream in = Files.newInputStream(path)) {
            properties.load(in);
        } catch (IOException ignored) {
            // Environment variables still apply.
        }
        return properties;
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (notBlank(value)) {
                return value;
            }
        }
        return "";
    }

    private static boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }
}
