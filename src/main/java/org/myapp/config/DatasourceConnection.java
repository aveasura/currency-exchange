package org.myapp.config;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatasourceConnection {
    private static final String DB_URL = "jdbc:sqlite:curr.db";

    public Connection connect() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");

        File dbFile = new File("curr.db");
        System.out.println("База данных будет создана здесь: " + dbFile.getAbsolutePath());

        Connection connection = DriverManager.getConnection(DB_URL);
        try (Statement statement = connection.createStatement()) {
            String createCurrenciesTable = """
                    CREATE TABLE IF NOT EXISTS Currencies (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        code TEXT UNIQUE NOT NULL,
                        full_name TEXT UNIQUE NOT NULL,
                        sign TEXT
                    );
                    """;
            String createExchangeRatesTable = """
                    CREATE TABLE IF NOT EXISTS ExchangeRates (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    base_currency_id INTEGER NOT NULL,
                    target_currency_id INTEGER NOT NULL,
                    rate DECIMAL(10, 6) NOT NULL,
                    UNIQUE(base_currency_id, target_currency_id),
                    FOREIGN KEY (base_currency_id) REFERENCES Currencies(id),
                    FOREIGN KEY (target_currency_id) REFERENCES Currencies(id)
                    );
                    """;

            statement.execute(createCurrenciesTable);
            statement.execute(createExchangeRatesTable);
        } catch (SQLException e) {
            System.out.println("Ошибка при работе с базой данных: " + e.getMessage());
            throw e;
        }

        return connection;
    }
}