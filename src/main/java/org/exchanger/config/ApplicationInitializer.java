package org.exchanger.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.exchanger.config.connection.ConnectionProvider;
import org.exchanger.config.connection.HikariDataSourceFactory;
import org.exchanger.config.connection.SqliteConnectionProvider;
import org.exchanger.config.database.DatabaseInitializer;
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
        HikariDataSource dataSource = createDataSource();
        ConnectionProvider connectionProvider = new SqliteConnectionProvider(dataSource);

        initializeDatabase(dataSource, connectionProvider);

        AppComponents components = createComponents(dataSource, connectionProvider);
        event.getServletContext().setAttribute(AppComponents.class.getName(), components);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        AppComponents components =
                (AppComponents) event.getServletContext().getAttribute(AppComponents.class.getName());

        closeDataSource(components);
    }

    private void closeDataSource(AppComponents components) {
        if (components == null) {
            return;
        }

        HikariDataSource dataSource = components.dataSource();
        if (dataSource != null) {
            dataSource.close();
        }
    }

    private HikariDataSource createDataSource() {
        DatabaseConfig config = new DatabaseConfigResolver().resolve();
        HikariDataSourceFactory factory = new HikariDataSourceFactory(config);
        return factory.create();
    }

    private void initializeDatabase(HikariDataSource dataSource, ConnectionProvider connectionProvider) {
        DatabaseInitializer databaseInitializer = new DatabaseInitializer(connectionProvider);
        try {
            databaseInitializer.initializeDatabase();
        } catch (DataAccessException e) {
            dataSource.close();
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    private AppComponents createComponents(HikariDataSource dataSource, ConnectionProvider connectionProvider) {
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

        return new AppComponents(
                dataSource,
                currencyService,
                exchangeRateService,
                exchangeService,
                objectMapper
        );
    }
}
