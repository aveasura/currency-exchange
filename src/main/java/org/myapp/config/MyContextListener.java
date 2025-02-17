package org.myapp.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.myapp.dao.CurrenciesDaoImpl;
import org.myapp.service.CurrenciesService;

import java.sql.Connection;
import java.sql.SQLException;

@WebListener
public class MyContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            DatasourceConnection datasourceConnection = new DatasourceConnection();
            Connection connection = datasourceConnection.connect();
            CurrenciesDaoImpl currenciesDaoImpl = new CurrenciesDaoImpl(connection);
            CurrenciesService service = new CurrenciesService(currenciesDaoImpl);

            sce.getServletContext().setAttribute("service", service);
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