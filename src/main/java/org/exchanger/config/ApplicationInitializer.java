package org.exchanger.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.exchanger.config.connection.ConnectionProvider;
import org.exchanger.config.connection.HikariDataSourceFactory;
import org.exchanger.config.connection.SqliteConnectionProvider;
import org.exchanger.dto.response.ExchangeRateResponse;
import org.exchanger.dto.response.UpdateExchangeRateResponse;
import org.exchanger.exception.DataAccessException;
import org.exchanger.mapper.CurrencyMapper;
import org.exchanger.mapper.ExchangeRateMapper;
import org.exchanger.mapper.ResponseMapper;
import org.exchanger.mapper.UpdateExchangeRateMapper;
import org.exchanger.model.ExchangeRate;
import org.exchanger.repository.CurrencyRepository;
import org.exchanger.repository.ExchangeRateRepository;
import org.exchanger.repository.jdbc.JdbcCurrencyRepository;
import org.exchanger.repository.jdbc.JdbcExchangeRateRepository;
import org.exchanger.service.CurrencyService;
import org.exchanger.service.ExchangeRateService;
import org.exchanger.service.ExchangeService;
import org.exchanger.service.impl.DefaultCurrencyService;
import org.exchanger.service.impl.DefaultExchangeRateService;
import org.exchanger.service.impl.DefaultExchangeService;
import tools.jackson.databind.ObjectMapper;

@WebListener
public class ApplicationInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();

        HikariDataSourceFactory factory = new HikariDataSourceFactory();
        HikariDataSource dataSource = factory.create();

        ConnectionProvider connectionProvider = new SqliteConnectionProvider(dataSource);
        DatabaseInitializer databaseInitializer = new DatabaseInitializer(connectionProvider);

        try {
            databaseInitializer.initializeDatabase();
        } catch (DataAccessException e) {
            dataSource.close();
            throw new RuntimeException("Failed to initialize database", e);
        }

        CurrencyMapper currencyMapper = new CurrencyMapper();
        ResponseMapper<ExchangeRate, ExchangeRateResponse> exchangeRateResponseMapper
                = new ExchangeRateMapper(currencyMapper);
        ResponseMapper<ExchangeRate, UpdateExchangeRateResponse> updateExchangeRateResponseMapper
                = new UpdateExchangeRateMapper(exchangeRateResponseMapper);

        CurrencyRepository currencyRepository = new JdbcCurrencyRepository(connectionProvider);
        ExchangeRateRepository exchangeRateRepository = new JdbcExchangeRateRepository(connectionProvider);

        CurrencyService currencyService = new DefaultCurrencyService(
                currencyRepository,
                currencyMapper
        );

        ExchangeRateService exchangeRateService = new DefaultExchangeRateService(
                currencyRepository,
                exchangeRateRepository,
                exchangeRateResponseMapper,
                updateExchangeRateResponseMapper
        );

        ExchangeService exchangeService = new DefaultExchangeService(
                currencyRepository,
                exchangeRateRepository,
                currencyMapper
        );

        ObjectMapper objectMapper = new ObjectMapper();

        context.setAttribute(ContextAttributes.DATA_SOURCE, dataSource);
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
