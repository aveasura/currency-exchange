package org.exchanger.config.connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class HikariDataSourceFactory {

    private static final String CATALINA_BASE_PROPERTY = "catalina.base";
    private static final String DATABASE_DIRECTORY_NAME = "data";
    private static final String DATABASE_FILE_NAME = "app.db";
    private static final String POOL_NAME = "CurrencyExchangePool";
    private static final String SQLITE_DATA_SOURCE_CLASS = "org.sqlite.SQLiteDataSource";
    private static final String URL_PROPERTY = "url";
    private static final String SQLITE_JDBC_PREFIX = "jdbc:sqlite:";
    private static final int MAX_POOL_SIZE = 2;

    public HikariDataSource create() {
        Path path = resolveDatabasePath();
        return createDataSource(path);
    }

    private Path resolveDatabasePath() {
        Path dbDir = Path.of(System.getProperty(CATALINA_BASE_PROPERTY), DATABASE_DIRECTORY_NAME);
        try {
            Files.createDirectories(dbDir);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create database directory", e);
        }
        return dbDir.resolve(DATABASE_FILE_NAME);
    }

    private HikariDataSource createDataSource(Path dbPath) {
        HikariConfig config = new HikariConfig();
        config.setPoolName(POOL_NAME);
        config.setDataSourceClassName(SQLITE_DATA_SOURCE_CLASS);
        config.addDataSourceProperty(URL_PROPERTY, SQLITE_JDBC_PREFIX + dbPath.toAbsolutePath());
        config.setMaximumPoolSize(MAX_POOL_SIZE);

        return new HikariDataSource(config);
    }
}
