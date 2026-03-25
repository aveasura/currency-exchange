package org.exchanger.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.exchanger.repository.CurrencyRepository;
import org.exchanger.repository.ExchangeRateRepository;
import org.exchanger.service.CurrencyService;
import org.exchanger.service.ExchangeRateService;
import tools.jackson.databind.ObjectMapper;

@WebListener
public class ApplicationInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ConnectionProvider connectionProvider = new ConnectionProvider();
        DataBaseManager dataBaseManager = new DataBaseManager(connectionProvider);

        CurrencyRepository currencyRepository = new CurrencyRepository(connectionProvider);
        sce.getServletContext().setAttribute("currencyRepository", currencyRepository);

        CurrencyService currencyService = new CurrencyService(currencyRepository);
        sce.getServletContext().setAttribute("currencyService", currencyService);

        ExchangeRateRepository exchangeRateRepository = new ExchangeRateRepository(connectionProvider);
        sce.getServletContext().setAttribute("exchangeRateRepository", exchangeRateRepository);

        ExchangeRateService exchangeRateService = new ExchangeRateService(exchangeRateRepository);
        sce.getServletContext().setAttribute("exchangeRateService", exchangeRateService);


        ObjectMapper objectMapper = new ObjectMapper();
        sce.getServletContext().setAttribute("objectMapper", objectMapper);

        try {
            dataBaseManager.initialize();
            dataBaseManager.initialDatabase();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
