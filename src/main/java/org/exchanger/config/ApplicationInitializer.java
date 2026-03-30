package org.exchanger.config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.exchanger.dto.request.CurrencyRequest;
import org.exchanger.dto.response.CurrencyResponse;
import org.exchanger.dto.response.ExchangeRateResponse;
import org.exchanger.mapper.CurrencyMapper;
import org.exchanger.mapper.ExchangeRateMapper;
import org.exchanger.mapper.RequestMapper;
import org.exchanger.mapper.ResponseMapper;
import org.exchanger.model.Currency;
import org.exchanger.model.ExchangeRate;
import org.exchanger.repository.CurrencyRepository;
import org.exchanger.repository.ExchangeRateRepository;
import org.exchanger.service.CurrencyService;
import org.exchanger.service.ExchangeRateService;
import org.exchanger.service.ExchangeService;
import tools.jackson.databind.ObjectMapper;

@WebListener
public class ApplicationInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();

        ConnectionProvider connectionProvider = new ConnectionProvider();
        DataBaseManager dataBaseManager = new DataBaseManager(connectionProvider);

        try {
            dataBaseManager.initialize();
            dataBaseManager.initializeDatabase();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }

        CurrencyMapper currencyMapper = new CurrencyMapper();
        RequestMapper<CurrencyRequest, Currency> currencyRequestMapper = currencyMapper;
        ResponseMapper<Currency, CurrencyResponse> currencyResponseMapper = currencyMapper;
        ResponseMapper<ExchangeRate, ExchangeRateResponse> exchangeRateResponseMapper
                = new ExchangeRateMapper(currencyResponseMapper);

        CurrencyRepository currencyRepository = new CurrencyRepository(connectionProvider);
        ExchangeRateRepository exchangeRateRepository = new ExchangeRateRepository(connectionProvider);

        CurrencyService currencyService = new CurrencyService(
                currencyRepository,
                currencyRequestMapper,
                currencyResponseMapper
        );

        ExchangeRateService exchangeRateService = new ExchangeRateService(
                currencyRepository,
                exchangeRateRepository,
                exchangeRateResponseMapper
        );

        ExchangeService exchangeService = new ExchangeService(
                currencyRepository,
                exchangeRateRepository,
                currencyResponseMapper
        );

        ObjectMapper objectMapper = new ObjectMapper();

        context.setAttribute("currencyRepository", currencyRepository);
        context.setAttribute("currencyService", currencyService);
        context.setAttribute("exchangeRateRepository", exchangeRateRepository);
        context.setAttribute("exchangeRateService", exchangeRateService);
        context.setAttribute("exchangeService", exchangeService);
        context.setAttribute("objectMapper", objectMapper);
    }
}
