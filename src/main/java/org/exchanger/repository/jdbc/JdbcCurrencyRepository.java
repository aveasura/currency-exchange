package org.exchanger.repository.jdbc;

import org.exchanger.config.connection.ConnectionProvider;
import org.exchanger.exception.CurrencyNotFoundException;
import org.exchanger.exception.DataAccessException;
import org.exchanger.exception.DuplicateEntityException;
import org.exchanger.model.Currency;
import org.exchanger.repository.CurrencyRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public final class JdbcCurrencyRepository extends AbstractJdbcRepository implements CurrencyRepository {
    private static final String INSERT_CURRENCY_SQL = """
            INSERT INTO currencies (code, full_name, sign)
            VALUES (?, ?, ?)
            RETURNING id
            """;

    private static final String SELECT_CURRENCY_BY_CODE_SQL = """
            SELECT id, code, full_name, sign
            FROM currencies
            WHERE code = ?
            """;

    private static final String SELECT_ALL_CURRENCIES_SQL = """
            SELECT id, code, full_name, sign
            FROM currencies
            ORDER BY id
            """;

    public JdbcCurrencyRepository(ConnectionProvider connectionProvider) {
        super(connectionProvider);
    }

    @Override
    public Long create(String code, String fullName, String sign) {
        try {
            return executeSingleResult(
                    INSERT_CURRENCY_SQL,
                    preparedStatement -> {
                        preparedStatement.setString(1, code);
                        preparedStatement.setString(2, fullName);
                        preparedStatement.setString(3, sign);
                    },
                    resultSet -> resultSet.getLong("id"),
                    () -> new DataAccessException("Create currency error: failed to assign id")
            );
        } catch (DataAccessException e) {
            if (isUniqueConstraintViolation(e)) {
                throw new DuplicateEntityException("Currency already exists", e);
            }
            throw e;
        }
    }

    @Override
    public Currency findByCode(String code) {
        return executeSingleResult(
                SELECT_CURRENCY_BY_CODE_SQL,
                preparedStatement -> preparedStatement.setString(1, code),
                this::mapCurrency,
                () -> new CurrencyNotFoundException(code));
    }

    @Override
    public List<Currency> findAll() {
        return executeList(SELECT_ALL_CURRENCIES_SQL, this::mapCurrency);
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
