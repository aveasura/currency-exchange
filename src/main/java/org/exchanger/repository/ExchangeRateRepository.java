package org.exchanger.repository;

import org.exchanger.config.ConnectionProvider;
import org.exchanger.model.Currency;
import org.exchanger.model.ExchangeRate;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateRepository {

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

    private final ConnectionProvider connectionProvider;

    public ExchangeRateRepository(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public List<ExchangeRate> findAll() {

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_EXISTING_RATES_SQL)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            List<ExchangeRate> exchangeRates = new ArrayList<>();
            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                BigDecimal rate = resultSet.getBigDecimal("rate");

                long baseId = resultSet.getLong("baseId");
                String baseCode = resultSet.getString("baseCode");
                String baseFullName = resultSet.getString("baseFullName");
                String baseSign = resultSet.getString("baseSign");

                long targetId = resultSet.getLong("targetId");
                String targetCode = resultSet.getString("targetCode");
                String targetFullName = resultSet.getString("targetFullName");
                String targetSign = resultSet.getString("targetSign");

                Currency baseCurrency = new Currency(baseId, baseFullName, baseCode, baseSign);
                Currency targetCurrency = new Currency(targetId, targetFullName, targetCode, targetSign);
                ExchangeRate exchangeRate = new ExchangeRate(id, baseCurrency, targetCurrency, rate);

                exchangeRates.add(exchangeRate);
            }

            return exchangeRates;
        } catch (SQLException e) {
            throw new RuntimeException("ошибка получения всех валют", e);
        }
    }

    public ExchangeRate find(Long baseCurrencyId, Long targetCurrencyId) {
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_PAIR_RATES_SQL)) {
            preparedStatement.setLong(1, baseCurrencyId);
            preparedStatement.setLong(2, targetCurrencyId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                throw new RuntimeException("не найден exchange rate");
            }
            long id = resultSet.getLong("id");
            BigDecimal rate = resultSet.getBigDecimal("rate");

            long baseId = resultSet.getLong("baseId");
            String baseFullName = resultSet.getString("baseFullName");
            String baseCode = resultSet.getString("baseCode");
            String baseSign = resultSet.getString("baseSign");

            long targetId = resultSet.getLong("targetId");
            String targetFullName = resultSet.getString("targetFullName");
            String targetCode = resultSet.getString("targetCode");
            String targetSign = resultSet.getString("targetSign");

            Currency base = new Currency(baseId, baseFullName, baseCode, baseSign);
            Currency target = new Currency(targetId, targetFullName, targetCode, targetSign);

            ExchangeRate exchangeRate = new ExchangeRate(id, base, target, rate);

            return exchangeRate;
        } catch (SQLException e) {
            throw new RuntimeException("ошибка поиска пары", e);
        }
    }

    public Long create(Currency base, Currency target, BigDecimal rate) {
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO_EXCHANGE_RATES)) {
            preparedStatement.setLong(1, base.getId());
            preparedStatement.setLong(2, target.getId());
            preparedStatement.setBigDecimal(3, rate);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong("id");
                }
                throw new RuntimeException("Не удалось получить id созданного exchange rate");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании exchange rate", e);
        }
    }
}
