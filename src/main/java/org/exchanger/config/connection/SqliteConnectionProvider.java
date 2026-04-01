package org.exchanger.config.connection;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class SqliteConnectionProvider implements ConnectionProvider {

    private static final String ENABLE_FOREIGN_KEYS_SQL = "PRAGMA foreign_keys = ON";

    private final DataSource dataSource;

    public SqliteConnectionProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = dataSource.getConnection();

        try (Statement statement = connection.createStatement()) {
            statement.execute(ENABLE_FOREIGN_KEYS_SQL);
        }

        return connection;
    }
}