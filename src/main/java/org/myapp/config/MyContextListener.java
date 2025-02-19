package org.myapp.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.myapp.dao.*;
import org.myapp.model.Currency;
import org.myapp.service.CurrenciesService;
import org.myapp.service.ExchangeRateService;

import java.sql.Connection;
import java.sql.SQLException;

@WebListener
public class MyContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            Connection connection = new DatasourceConnection().connect();
            CurrencyDao currencyDao = new CurrencyDaoImpl(connection);
            ExchangeRateDao exchangeRateDao = new ExchangeRateDaoImpl(connection);

            CurrenciesService service = new CurrenciesService(currencyDao);
            ExchangeRateService exchangeRateService = new ExchangeRateService(exchangeRateDao, currencyDao);

            sce.getServletContext().setAttribute("service", service);
            sce.getServletContext().setAttribute("ExchangeRatesService", exchangeRateService);
        } catch (SQLException e) {
            System.out.println("Ошибка подключения или работы с базой данных: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        Connection connection = (Connection) sce.getServletContext().getAttribute("connection");
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Соединение с базой данных закрыто.");
            } catch (SQLException e) {
                System.out.println("Ошибка при закрытии соединения: " + e.getMessage());
            }
        }
    }
}