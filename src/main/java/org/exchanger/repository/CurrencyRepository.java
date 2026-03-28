package org.exchanger.repository;

import org.exchanger.config.ConnectionProvider;
import org.exchanger.model.Currency;

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
            SELECT *
            FROM currencies""";

    public CurrencyRepository(ConnectionProvider connectionProvider) {
        super(connectionProvider);
    }

    public Currency findCurrency(String code) {
        Currency currency = executeSingleResult(
                SELECT_BY_CODE_SQL,
                preparedStatement -> {
                    preparedStatement.setString(1, code);
                },
                resultSet -> new Currency(
                        resultSet.getLong("id"),
                        resultSet.getString("full_name"),
                        resultSet.getString("code"),
                        resultSet.getString("sign")
                )
        );

        return currency;
    }

    public List<Currency> findAll() {
        List<Currency> currencies = executeList(
                SELECT_ALL_SQL,
                resultSet -> new Currency(
                        resultSet.getLong("id"),
                        resultSet.getString("full_name"),
                        resultSet.getString("code"),
                        resultSet.getString("sign")
                )
        );

        return currencies;
    }

    public Long create(Currency currency) {
        Long id = executeSingleResult(
                INSERT_SQL,
                preparedStatement -> {
                    preparedStatement.setString(1, currency.getCode());
                    preparedStatement.setString(2, currency.getFullName());
                    preparedStatement.setString(3, currency.getSign());
                },
                resultSet -> resultSet.getLong("id")
        );

        return id;
    }
}
