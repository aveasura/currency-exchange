package org.exchanger.config;

import java.nio.file.Path;

public final class DatabaseConfigResolver {

    private static final String DB_PATH_PROPERTY = "app.db.path";
    private static final String DB_PATH_ENV = "APP_DB_PATH";
    private static final String CATALINA_BASE_PROPERTY = "catalina.base";
    private static final String DATABASE_DIRECTORY_NAME = "data";
    private static final String DATABASE_FILE_NAME = "app.db";

    private static final String POOL_NAME_PROPERTY = "app.db.pool.name";
    private static final String MAX_POOL_SIZE_PROPERTY = "app.db.pool.max-size";

    private static final String DEFAULT_POOL_NAME = "CurrencyExchangePool";
    private static final int DEFAULT_MAX_POOL_SIZE = 2;

    public DatabaseConfig resolve() {
        Path databasePath = resolveDatabasePath();
        int maxPoolSize = Integer.parseInt(
                System.getProperty(MAX_POOL_SIZE_PROPERTY, String.valueOf(DEFAULT_MAX_POOL_SIZE))
        );
        String poolName = System.getProperty(POOL_NAME_PROPERTY, DEFAULT_POOL_NAME);

        return new DatabaseConfig(databasePath, maxPoolSize, poolName);
    }

    private Path resolveDatabasePath() {
        String explicitPath = System.getProperty(DB_PATH_PROPERTY);
        if (isBlank(explicitPath)) {
            explicitPath = System.getenv(DB_PATH_ENV);
        }

        if (!isBlank(explicitPath)) {
            return Path.of(explicitPath);
        }

        String catalinaBase = System.getProperty(CATALINA_BASE_PROPERTY);
        if (isBlank(catalinaBase)) {
            throw new IllegalStateException(
                    "Database path is not configured. " +
                    "Set system property 'app.db.path', env 'APP_DB_PATH', or 'catalina.base'."
            );
        }

        return Path.of(catalinaBase, DATABASE_DIRECTORY_NAME, DATABASE_FILE_NAME);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}