package com.bank.persistence;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton that opens JDBC connections with {@code DriverManager}.
 * Credentials come from {@code db.properties}. Tests set {@code bank.test.db=true} so they hit
 * {@code bank_cli_test} instead of the live {@code bank_cli} database.
 */
public class ConnectionFactory {
    private static final ConnectionFactory connectionFactory = new ConnectionFactory();
    private final Properties props = new Properties();

    private ConnectionFactory() {
        try {
            loadProperties();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load db.properties", e);
        }
    }

    public static ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    url(),
                    props.getProperty("DB_USER"),
                    props.getProperty("DB_PASSWORD", "")
            );
        } catch (SQLException e) {
            throw new IllegalStateException("Could not connect to the database", e);
        }
    }

    /** Live app uses DB_URL; {@code mvn test} switches to TEST_DB_URL. */
    private String url() {
        if (Boolean.parseBoolean(System.getProperty("bank.test.db", "false"))) {
            String testUrl = props.getProperty("TEST_DB_URL");
            if (testUrl != null && !testUrl.isBlank()) {
                return testUrl;
            }
        }
        return props.getProperty("DB_URL");
    }

    private void loadProperties() throws IOException {
        try (FileReader reader = new FileReader("src/main/resources/db.properties")) {
            props.load(reader);
            return;
        } catch (IOException ignored) {
            // Fall back to the classpath copy packaged by Maven.
        }
        try (InputStream in = ConnectionFactory.class.getResourceAsStream("/db.properties")) {
            if (in == null) {
                throw new IOException("db.properties was not found");
            }
            props.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        }
    }
}
