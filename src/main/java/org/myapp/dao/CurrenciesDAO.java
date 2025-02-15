package org.myapp.dao;

import org.myapp.model.Currency;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrenciesDAO {
    private final Connection connection;

    public CurrenciesDAO(Connection connection) {
        this.connection = connection;
    }

    public int addCurrency(Currency currency) {
        String sql = "INSERT INTO Currencies (code, full_name, sign) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, currency.getCode());
            statement.setString(2, currency.getFullName());
            statement.setString(3, currency.getSign());

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                // Используем запрос для получения последнего сгенерированного ID [>>SQLite<<]
                try (Statement lastIdStmt = connection.createStatement();
                     ResultSet rs = lastIdStmt.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при добавлении валюты: " + e.getMessage());
        }
        return -1;
    }

    public List<Currency> getCurrencies() {
        List<Currency> currencies = new ArrayList<>();

        String sql = "SELECT * FROM Currencies";
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            ResultSet rs = statement.executeQuery();

            // Обработка результата запроса
            while (rs.next()) {
                int id = rs.getInt("id");
                String code = rs.getString("code");
                String fullName = rs.getString("full_name");
                String sign = rs.getString("sign");

                // Создание модели для каждой валюты
                Currency currency = new Currency(id, code, fullName, sign);
                currencies.add(currency);
            }
        } catch (SQLException e) {
            System.out.println("Ошибка выгрузки всех валют " + e.getMessage());
        }

        return currencies;
    }
}
