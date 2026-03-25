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

    private static final String FIND_ALL_EXISTING_RATES = """
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

    private final ConnectionProvider connectionProvider;

    public ExchangeRateRepository(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public List<ExchangeRate> findAll() {

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_EXISTING_RATES)){

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
}
