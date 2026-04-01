package org.exchanger.config.database;

import org.exchanger.config.connection.ConnectionProvider;
import org.exchanger.exception.DataAccessException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseInitializer {

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

    // ID стартуют с 0 намеренно. Последующие вставки SQLite нумерует корректно с учетом уже существующих записей.
    private static final String INSERT_DEFAULT_CURRENCIES_SQL = """
            INSERT OR IGNORE INTO currencies(id, code, full_name, sign)
            VALUES (0, 'USD', 'United States dollar', '$'),
                   (1, 'EUR', 'Euro', '€'),
                   (2, 'RUB', 'Russian ruble', '₽'),
                   (3, 'GBP', 'British pound', '£'),
                   (4, 'JPY', 'Japanese yen', '¥'),
                   (5, 'CHF', 'Swiss franc', 'Fr.'),
                   (6, 'CAD', 'Canadian dollar', 'C$');
            """;

    private static final String INSERT_DEFAULT_EXCHANGE_RATES_SQL = """
            INSERT OR IGNORE INTO exchange_rates(id, base_currency_id, target_currency_id, rate)
            VALUES (0, 0, 1, 0.86),
                   (1, 0, 2, 92.50),
                   (2, 0, 3, 0.74),
                   (3, 0, 4, 149.30);
            """;

    private final ConnectionProvider connectionProvider;

    public DatabaseInitializer(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public void initializeDatabase() {
        try (Connection connection = connectionProvider.getConnection()) {
            connection.setAutoCommit(false);

            try (Statement statement = connection.createStatement()) {
                statement.execute(CREATE_CURRENCIES_SQL);
                statement.execute(CREATE_CURRENCIES_CODE_INDEX_SQL);

                statement.execute(CREATE_EXCHANGE_RATES_SQL);
                statement.execute(CREATE_EXCHANGE_RATES_PAIR_INDEX_SQL);

                statement.execute(INSERT_DEFAULT_CURRENCIES_SQL);
                statement.execute(INSERT_DEFAULT_EXCHANGE_RATES_SQL);
            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    e.addSuppressed(rollbackEx);
                }
                throw e;
            }

            connection.commit();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to initialize database", e);
        }
    }
}
