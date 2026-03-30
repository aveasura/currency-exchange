package org.exchanger.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseManager {

    private static final String SQLITE_DRIVER_CLASS = "org.sqlite.JDBC";

    private static final String CREATE_CURRENCIES_SQL = """
            CREATE TABLE IF NOT EXISTS currencies (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                code TEXT NOT NULL CHECK (code GLOB '[A-Z][A-Z][A-Z]'),
                full_name TEXT NOT NULL CHECK (
                    length(trim(full_name)) > 0
                    AND length(full_name) <= 50
                ),
                sign TEXT NOT NULL CHECK (
                    length(trim(sign)) > 0
                    AND length(sign) <= 5
                )
            );
            """;

    private static final String CREATE_CURRENCIES_CODE_INDEX_SQL = """
            CREATE UNIQUE INDEX IF NOT EXISTS idx_currencies_code
            ON currencies(code);
            """;

    private static final String CREATE_EXCHANGE_RATES_SQL = """
            CREATE TABLE IF NOT EXISTS exchange_rates (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                base_currency_id INTEGER NOT NULL CHECK (base_currency_id >= 0),
                target_currency_id INTEGER NOT NULL CHECK (target_currency_id >= 0),
                rate NUMERIC NOT NULL CHECK (
                    rate > 0
                    AND round(rate, 6) = rate
                ),
                FOREIGN KEY (base_currency_id) REFERENCES currencies(id),
                FOREIGN KEY (target_currency_id) REFERENCES currencies(id),
                CHECK (base_currency_id <> target_currency_id)
            );
            """;

    private static final String CREATE_EXCHANGE_RATES_PAIR_INDEX_SQL = """
            CREATE UNIQUE INDEX IF NOT EXISTS idx_exchange_rate_base_target
            ON exchange_rates(base_currency_id, target_currency_id);
            """;

    private static final String INSERT_DEFAULT_CURRENCIES_SQL = """
            INSERT OR IGNORE INTO currencies(id, code, full_name, sign)
            VALUES (0, 'USD', 'United States dollar', '$'),
                   (1, 'EUR', 'Euro', '€'),
                   (2, 'RUB', 'Russian ruble', '₽');
            """;

    private static final String INSERT_DEFAULT_EXCHANGE_RATES_SQL = """
            INSERT OR IGNORE INTO exchange_rates(id, base_currency_id, target_currency_id, rate)
            VALUES (0,0,1,0.86)
            """;

    private final ConnectionProvider connectionProvider;

    public DatabaseManager(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public void initialize() throws ClassNotFoundException {
        Class.forName(SQLITE_DRIVER_CLASS);
    }

    public void initializeDatabase() {
        try (Connection connection = connectionProvider.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute(CREATE_CURRENCIES_SQL);
            statement.execute(CREATE_CURRENCIES_CODE_INDEX_SQL);

            statement.execute(CREATE_EXCHANGE_RATES_SQL);
            statement.execute(CREATE_EXCHANGE_RATES_PAIR_INDEX_SQL);

            statement.execute(INSERT_DEFAULT_CURRENCIES_SQL);
            statement.execute(INSERT_DEFAULT_EXCHANGE_RATES_SQL);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create initial tables", e);
        }
    }
}
