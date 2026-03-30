package org.exchanger.repository;

import org.exchanger.config.ConnectionProvider;
import org.exchanger.exception.CurrencyNotFoundException;
import org.exchanger.exception.DataAccessException;
import org.exchanger.model.Currency;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CurrencyRepository extends BaseJdbcRepository {
    private static final String INSERT_SQL = """
            INSERT INTO currencies (code, full_name, sign)
            VALUES (?, ?, ?)
            RETURNING id
            """;

    private static final String SELECT_BY_CODE_SQL = """
            SELECT id, code, full_name, sign
            FROM currencies
            WHERE code = ?""";

    private static final String SELECT_ALL_SQL = """
            SELECT id, code, full_name, sign
            FROM currencies
            ORDER BY id""";

    public CurrencyRepository(ConnectionProvider connectionProvider) {
        super(connectionProvider);
    }

    public Long create(String code, String name, String sign) {
        return executeSingleResult(
                INSERT_SQL,
                preparedStatement -> {
                    preparedStatement.setString(1, code);
                    preparedStatement.setString(2, name);
                    preparedStatement.setString(3, sign);
                },
                resultSet -> resultSet.getLong("id"),
                () -> new DataAccessException("Create currency error, failed to assign id")
        );
    }

    public Currency find(String code) {
        return executeSingleResult(
                SELECT_BY_CODE_SQL,
                preparedStatement -> preparedStatement.setString(1, code),
                this::mapCurrency,
                () -> new CurrencyNotFoundException(code));
    }

    public List<Currency> findAll() {
        return executeList(SELECT_ALL_SQL, this::mapCurrency);
    }

    private Currency mapCurrency(ResultSet resultSet) throws SQLException {
        return new Currency(
                resultSet.getLong("id"),
                resultSet.getString("full_name"),
                resultSet.getString("code"),
                resultSet.getString("sign")
        );
    }
}
