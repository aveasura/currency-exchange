package org.exchanger.repository;

import org.exchanger.config.ConnectionProvider;
import org.exchanger.model.Currency;
import org.exchanger.model.ExchangeRate;

import java.math.BigDecimal;
import java.util.List;

public class ExchangeRateRepository extends BaseJdbcRepository {
    private static final String GET_ALL_EXISTING_RATES_SQL = """
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

    private static final String GET_PAIR_RATES_SQL =
            GET_ALL_EXISTING_RATES_SQL + """
                    WHERE er.base_currency_id = ?
                    AND er.target_currency_id = ?
                    """;

    private static final String INSERT_INTO_EXCHANGE_RATES = """
            INSERT INTO exchange_rates(base_currency_id, target_currency_id, rate)
            VALUES(?, ?, ?)
            RETURNING id
            """;

    public ExchangeRateRepository(ConnectionProvider connectionProvider) {
        super(connectionProvider);
    }

    public List<ExchangeRate> findAll() {
        List<ExchangeRate> exchangeRates = executeList(
                GET_ALL_EXISTING_RATES_SQL,
                resultSet -> new ExchangeRate(
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
                )
        );

        return exchangeRates;
    }

    public ExchangeRate find(Long baseCurrencyId, Long targetCurrencyId) {
        ExchangeRate exchangeRate = executeSingleResult(
                GET_PAIR_RATES_SQL,
                preparedStatement -> {
                    preparedStatement.setLong(1, baseCurrencyId);
                    preparedStatement.setLong(2, targetCurrencyId);
                },
                resultSet -> new ExchangeRate(
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
                )
        );

        return exchangeRate;
    }

    public Long create(Currency base, Currency target, BigDecimal rate) {
        Long id = executeSingleResult(
                INSERT_INTO_EXCHANGE_RATES,
                preparedStatement -> {
                    preparedStatement.setLong(1, base.getId());
                    preparedStatement.setLong(2, target.getId());
                    preparedStatement.setBigDecimal(3, rate);
                },
                resultSet -> resultSet.getLong("id")
        );

        return id;
    }
}
