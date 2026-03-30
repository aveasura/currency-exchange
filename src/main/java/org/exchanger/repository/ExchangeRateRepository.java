package org.exchanger.repository;

import org.exchanger.config.ConnectionProvider;
import org.exchanger.exception.DataAccessException;
import org.exchanger.model.Currency;
import org.exchanger.model.ExchangeRate;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ExchangeRateRepository extends BaseJdbcRepository {
    private static final String BASE_SELECT_SQL = """
            SELECT er.id AS id,
                    er.rate AS rate,
                    base.id AS baseId,
                    base.code AS baseCode,
                    base.full_name AS baseFullName,
                    base.sign AS baseSign,
                    target.id AS targetId,
                    target.code AS targetCode,
                    target.full_name AS targetFullName,
                    target.sign AS targetSign
            FROM exchange_rates er
            JOIN currencies base ON er.base_currency_id = base.id
            JOIN currencies target ON er.target_currency_id = target.id
            """;

    private static final String SELECT_ALL_SQL = BASE_SELECT_SQL + """
            ORDER BY er.id
            """;

    private static final String SELECT_BY_CURRENCIES_ID_SQL = BASE_SELECT_SQL + """
            WHERE er.base_currency_id = ?
            AND er.target_currency_id = ?
            """;

    private static final String INSERT_SQL = """
            INSERT INTO exchange_rates(base_currency_id, target_currency_id, rate)
            VALUES(?, ?, ?)
            RETURNING id
            """;

    private static final String UPDATE_EXCHANGE_RATE_SQL = """
            UPDATE exchange_rates
            SET rate = ?
            WHERE id = ?
            RETURNING rate
            """;

    public ExchangeRateRepository(ConnectionProvider connectionProvider) {
        super(connectionProvider);
    }

    public Long create(Long baseCurrencyId, Long targetCurrencyId, BigDecimal rate) {
        return executeSingleResult(
                INSERT_SQL,
                preparedStatement -> {
                    preparedStatement.setLong(1, baseCurrencyId);
                    preparedStatement.setLong(2, targetCurrencyId);
                    preparedStatement.setBigDecimal(3, rate);
                },
                resultSet -> resultSet.getLong("id"),
                () -> new DataAccessException("Create exchange rate error, failed assign id"));
    }

    public Optional<ExchangeRate> find(Long baseCurrencyId, Long targetCurrencyId) {
        List<ExchangeRate> result = executeList(
                SELECT_BY_CURRENCIES_ID_SQL,
                preparedStatement -> {
                    preparedStatement.setLong(1, baseCurrencyId);
                    preparedStatement.setLong(2, targetCurrencyId);
                },
                this::mapExchangeRate
        );
        return result.stream().findFirst();
    }

    public List<ExchangeRate> findAll() {
        return executeList(
                SELECT_ALL_SQL,
                this::mapExchangeRate
        );
    }

    public void update(Long exchangeRateId, BigDecimal rate) {
        executeSingleResult(
                UPDATE_EXCHANGE_RATE_SQL,
                preparedStatement -> {
                    preparedStatement.setBigDecimal(1, rate);
                    preparedStatement.setLong(2, exchangeRateId);
                },
                resultSet -> resultSet.getBigDecimal("rate"),
                () -> new DataAccessException("Update exchange rate error")
        );
    }

    private ExchangeRate mapExchangeRate(ResultSet resultSet) throws SQLException {
        return new ExchangeRate(
                resultSet.getLong("id"),
                new Currency(
                        resultSet.getLong("baseId"),
                        resultSet.getString("baseFullName"),
                        resultSet.getString("baseCode"),
                        resultSet.getString("baseSign")
                ),
                new Currency(
                        resultSet.getLong("targetId"),
                        resultSet.getString("targetFullName"),
                        resultSet.getString("targetCode"),
                        resultSet.getString("targetSign")
                ),
                resultSet.getBigDecimal("rate")
        );
    }
}
