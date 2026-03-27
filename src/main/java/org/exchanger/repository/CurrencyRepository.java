package org.exchanger.repository;

import org.exchanger.config.ConnectionProvider;
import org.exchanger.model.Currency;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CurrencyRepository {

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

    private final ConnectionProvider connectionProvider;

    public CurrencyRepository(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public Currency findCurrency(String code) {
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_CODE_SQL)) {
            preparedStatement.setString(1, code);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    long id = rs.getLong("id");
                    String fullName = rs.getString("full_name");
                    String currencyCode = rs.getString("code");
                    String sign = rs.getString("sign");

                    return new Currency(id, fullName, currencyCode, sign);
                }
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске валюты: " + code, e);
        }
    }

    public List<Currency> findAll() {
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Currency> currencies = new ArrayList<>();
            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                String fullName = resultSet.getString("full_name");
                String code = resultSet.getString("code");
                String sign = resultSet.getString("sign");

                Currency currency = new Currency(id, fullName, code, sign);

                currencies.add(currency);
            }

            return currencies;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске валют", e);
        }
    }

    public Long create(Currency currency) {
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL)) {
            preparedStatement.setString(1, currency.getCode());
            preparedStatement.setString(2, currency.getFullName());
            preparedStatement.setString(3, currency.getSign());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong("id");
                }
                throw new RuntimeException("ошибка получения id созданной валюты");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании валюты: " + currency, e);
        }
    }
}
