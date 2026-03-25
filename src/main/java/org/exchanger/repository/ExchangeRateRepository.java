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

    private static final String GET_PAIR_RATES_SQL = GET_ALL_EXISTING_RATES_SQL + "WHERE baseId = ? AND targetId = ?";

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
                int id = resultSet.getInt("id");
                BigDecimal rate = resultSet.getBigDecimal("rate");

                int baseId = resultSet.getInt("baseId");
                String baseCode = resultSet.getString("baseCode");
                String baseFullName = resultSet.getString("baseFullName");
                String baseSign = resultSet.getString("baseSign");

                int targetId = resultSet.getInt("targetId");
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

    public ExchangeRate find(Integer baseCurrencyId, Integer targetCurrencyId) {
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_PAIR_RATES_SQL)) {
            preparedStatement.setInt(1, baseCurrencyId);
            preparedStatement.setInt(2, targetCurrencyId);

            ResultSet resultSet = preparedStatement.executeQuery();
            int id = resultSet.getInt("id");
            BigDecimal rate = resultSet.getBigDecimal("rate");

            int baseId = resultSet.getInt("baseId");
            String baseFullName = resultSet.getString("baseFullName");
            String baseCode = resultSet.getString("baseCode");
            String baseSign = resultSet.getString("baseSign");

            int targetId = resultSet.getInt("targetId");
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
}
