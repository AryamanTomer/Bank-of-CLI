package com.bank.util;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class ConnectionFactory {
    private static final Object LOCK = new Object();
    private static EmbeddedPostgres postgres;
    private static boolean schemaReady;

    private ConnectionFactory() {
    }

    public static Connection getConnection() throws SQLException {
        ensureStarted();
        Connection connection = postgres.getPostgresDatabase().getConnection();
        ensureSchema(connection);
        return connection;
    }

    private static void ensureStarted() throws SQLException {
        if (postgres != null) {
            return;
        }
        synchronized (LOCK) {
            if (postgres != null) {
                return;
            }
            try {
                postgres = EmbeddedPostgres.builder()
                        .setDataDirectory(dataDirectory().toFile())
                        .setCleanDataDirectory(false)
                        .setPort(port())
                        .setOverrideWorkingDirectory(Path.of("data", "pg-bin").toFile())
                        .start();
            } catch (IOException e) {
                throw new SQLException("Failed to start PostgreSQL", e);
            }
        }
    }

    private static Path dataDirectory() {
        return Path.of("data", isTest() ? "pg-test" : "pg");
    }

    private static int port() {
        return isTest() ? 55433 : 55432;
    }

    private static boolean isTest() {
        return Boolean.parseBoolean(System.getProperty("bank.test.db", "false"));
    }

    private static void ensureSchema(Connection connection) throws SQLException {
        if (schemaReady) {
            return;
        }
        synchronized (LOCK) {
            if (schemaReady) {
                return;
            }
            String schema = loadSchema();
            try (Statement statement = connection.createStatement()) {
                for (String raw : schema.split(";")) {
                    String sql = raw.strip();
                    if (!sql.isEmpty()) {
                        statement.execute(sql);
                    }
                }
            }
            schemaReady = true;
        }
    }

    private static String loadSchema() throws SQLException {
        Path localSchema = Path.of("schema.sql");
        if (Files.exists(localSchema)) {
            try {
                return Files.readString(localSchema, StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new SQLException("Failed to read schema.sql", e);
            }
        }
        try (InputStream in = ConnectionFactory.class.getResourceAsStream("/schema.sql")) {
            if (in == null) {
                throw new SQLException("schema.sql was not found in the project folder or classpath");
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new SQLException("Failed to read schema.sql", e);
        }
    }
}
