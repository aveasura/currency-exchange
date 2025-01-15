package org.myapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatasourceConnection {
    private static final String DB_URL = "jdbc:sqlite:curr.db";

    public void connect() {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            if (connection != null) {
                System.out.println("Подключение с SQLite установлено.");

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
                    System.out.println("Таблица Currency создана.");

                    statement.execute(createExchangeRatesTable);
                    System.out.println("Таблица ExchangeRates создана");
                }
            }
        } catch (SQLException e) {
            System.out.println("Ошибка подключения: " + e.getMessage());
        }
    }
}