package org.exchanger.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.exchanger.config.connection.ConnectionProvider;
import org.exchanger.config.connection.HikariDataSourceFactory;
import org.exchanger.config.connection.SqliteConnectionProvider;
import org.exchanger.dto.request.CurrencyRequest;
import org.exchanger.dto.response.CurrencyResponse;
import org.exchanger.dto.response.ExchangeRateResponse;
import org.exchanger.exception.DataAccessException;
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

        HikariDataSourceFactory factory = new HikariDataSourceFactory();
        HikariDataSource dataSource = factory.create();
        context.setAttribute(ContextAttributes.DATA_SOURCE, dataSource);

        ConnectionProvider connectionProvider = new SqliteConnectionProvider(dataSource);
        DatabaseInitializer databaseInitializer = new DatabaseInitializer(connectionProvider);

        try {
            databaseInitializer.initializeDatabase();
        } catch (DataAccessException e) {
            dataSource.close();
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

        context.setAttribute(ContextAttributes.CURRENCY_REPOSITORY, currencyRepository);
        context.setAttribute(ContextAttributes.EXCHANGE_RATE_REPOSITORY, exchangeRateRepository);
        context.setAttribute(ContextAttributes.CURRENCY_SERVICE, currencyService);
        context.setAttribute(ContextAttributes.EXCHANGE_RATE_SERVICE, exchangeRateService);
        context.setAttribute(ContextAttributes.EXCHANGE_SERVICE, exchangeService);
        context.setAttribute(ContextAttributes.OBJECT_MAPPER, objectMapper);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        HikariDataSource dataSource =
                (HikariDataSource) event.getServletContext().getAttribute(ContextAttributes.DATA_SOURCE);

        if (dataSource != null) {
            dataSource.close();
        }
    }
}
