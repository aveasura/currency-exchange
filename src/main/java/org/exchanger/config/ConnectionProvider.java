package org.exchanger.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionProvider {

    private static final String JDBC_URL = "jdbc:sqlite:C:/Users/aveasura/IdeaProjects/currency-exchange/app.db";
    private static final String ENABLE_FOREIGN_KEYS_SQL = "PRAGMA foreign_keys = ON";

    public Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(JDBC_URL);

        try (Statement statement = connection.createStatement()) {
            statement.execute(ENABLE_FOREIGN_KEYS_SQL);
        }

        return connection;
    }
}
