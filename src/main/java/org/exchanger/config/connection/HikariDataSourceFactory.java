package org.exchanger.config.connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.exchanger.config.DatabaseConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class HikariDataSourceFactory {

    private static final String SQLITE_DATA_SOURCE_CLASS = "org.sqlite.SQLiteDataSource";
    private static final String URL_PROPERTY = "url";
    private static final String SQLITE_JDBC_PREFIX = "jdbc:sqlite:";

    private final DatabaseConfig config;

    public HikariDataSourceFactory(DatabaseConfig config) {
        this.config = config;
    }

    public HikariDataSource create() {
        ensureParentDirectoryExists(config.databasePath());

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setPoolName(config.poolName());
        hikariConfig.setDataSourceClassName(SQLITE_DATA_SOURCE_CLASS);
        hikariConfig.addDataSourceProperty(URL_PROPERTY, SQLITE_JDBC_PREFIX + config.databasePath().toAbsolutePath());
        hikariConfig.setMaximumPoolSize(config.maxPoolSize());

        return new HikariDataSource(hikariConfig);
    }

    private void ensureParentDirectoryExists(Path databasePath) {
        Path parent = databasePath.getParent();
        if (parent == null) {
            return;
        }

        try {
            Files.createDirectories(parent);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create database directory: " + parent, e);
        }
    }
}