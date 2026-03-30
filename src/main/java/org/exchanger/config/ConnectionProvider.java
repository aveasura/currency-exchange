package org.exchanger.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class ConnectionProvider {

    private static final Logger logger = Logger.getLogger(ConnectionProvider.class.getName());
    private static final String ENABLE_FOREIGN_KEYS_SQL = "PRAGMA foreign_keys = ON";

    private final String jdbcUrl;

    public ConnectionProvider() {
        Path dbPath = resolveDatabasePath();
        this.jdbcUrl = "jdbc:sqlite:" + dbPath.toAbsolutePath();
        logger.info("SQLite database path: " + dbPath.toAbsolutePath());
    }

    public Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(jdbcUrl);

        try (Statement statement = connection.createStatement()) {
            statement.execute(ENABLE_FOREIGN_KEYS_SQL);
        }

        return connection;
    }

    private Path resolveDatabasePath() {
        Path dbDir = Path.of(System.getProperty("catalina.base"), "data");

        try {
            Files.createDirectories(dbDir);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create database directory", e);
        }

        return dbDir.resolve("app.db");
    }
}