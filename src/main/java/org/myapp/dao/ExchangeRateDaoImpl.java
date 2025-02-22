package org.myapp.dao;

import org.myapp.model.Currency;
import org.myapp.model.ExchangeRate;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateDaoImpl implements ExchangeRateDao {

    private final Connection connection;

    public ExchangeRateDaoImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public int saveExchangeRate(Currency currencyFrom, Currency currencyTo, BigDecimal exchangeRate) {
        final String sql = "INSERT INTO ExchangeRates(base_currency_id, target_currency_id, rate) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, currencyFrom.getId());
            stmt.setInt(2, currencyTo.getId());
            stmt.setBigDecimal(3, exchangeRate);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (Statement lastIdStmt = connection.createStatement();
                     ResultSet rs = lastIdStmt.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при добавлении данных в таблицу ExchangeRate " + e.getMessage());
        }

        return -1;
    }

    @Override
    public List<ExchangeRate> findAll() {
        List<ExchangeRate> rates = new ArrayList<>();

        final String sql = "SELECT * FROM ExchangeRates";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                int baseCurrencyId = rs.getInt("base_currency_id");
                int target_currency_id = rs.getInt("target_currency_id");
                BigDecimal rate = rs.getBigDecimal("rate");

                ExchangeRate exchangeRate = new ExchangeRate(id, baseCurrencyId, target_currency_id, rate);
                rates.add(exchangeRate);
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при получении листа ExchangeRate" + e.getMessage());
        }

        return rates;
    }
}
