package org.exchanger.repository;

import org.exchanger.config.connection.ConnectionProvider;
import org.exchanger.config.connection.SqliteConnectionProvider;
import org.exchanger.config.database.DatabaseInitializer;
import org.exchanger.model.Currency;
import org.exchanger.model.ExchangeRate;
import org.exchanger.repository.jdbc.JdbcCurrencyRepository;
import org.exchanger.repository.jdbc.JdbcExchangeRateRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sqlite.SQLiteDataSource;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class JdbcExchangeRateRepositoryTest {

    private Path dbFile;
    private JdbcExchangeRateRepository repository;
    private JdbcCurrencyRepository currencyRepository;

    @BeforeEach
    void setUp() throws IOException {
        dbFile = Files.createTempFile("exchange-rate-test-", ".db");

        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite:" + dbFile);

        ConnectionProvider connectionProvider = new SqliteConnectionProvider(dataSource);
        new DatabaseInitializer(connectionProvider).initializeDatabase();

        repository = new JdbcExchangeRateRepository(connectionProvider);
        currencyRepository = new JdbcCurrencyRepository(connectionProvider);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(dbFile);
    }

    @Test
    void shouldFindExchangeRateByBaseCurrencyIdAndTargetCurrencyId() {
        Currency base = new Currency(0L, "United States dollar", "USD", "$");
        Currency target = new Currency(1L, "Euro", "EUR", "€");

        ExchangeRate exchangeRate = repository
                .findByBaseCurrencyIdAndTargetCurrencyId(0L, 1L)
                .orElseThrow(() -> new AssertionError("Exchange rate not found"));

        assertAll(
                () -> assertEquals(0L, exchangeRate.id()),
                () -> assertEquals(base, exchangeRate.baseCurrency()),
                () -> assertEquals(target, exchangeRate.targetCurrency()),
                () -> assertEquals(new BigDecimal("0.86"), exchangeRate.rate())
        );
    }

    @Test
    void shouldReturnEmptyWhenExchangeRateNotFound() {
        Optional<ExchangeRate> exchangeRate =
                repository.findByBaseCurrencyIdAndTargetCurrencyId(10L, 20L);

        assertFalse(exchangeRate.isPresent());
    }

    @Test
    void shouldCreateExchangeRate() {
        Long baseCurrencyId = currencyRepository.create("XAA", "Test currency A", "A");
        Long targetCurrencyId = currencyRepository.create("XBB", "Test currency B", "B");

        assertFalse(
                repository.findByBaseCurrencyIdAndTargetCurrencyId(baseCurrencyId, targetCurrencyId)
                        .isPresent()
        );

        Long id = repository.create(baseCurrencyId, targetCurrencyId, new BigDecimal("10"));

        ExchangeRate saved = repository.findByBaseCurrencyIdAndTargetCurrencyId(baseCurrencyId, targetCurrencyId)
                .orElseThrow(() -> new AssertionError("Exchange rate not found"));

        assertAll(
                () -> assertEquals(id, saved.id()),
                () -> assertEquals(baseCurrencyId, saved.baseCurrency().id()),
                () -> assertEquals(targetCurrencyId, saved.targetCurrency().id()),
                () -> assertEquals(new BigDecimal("10"), saved.rate())
        );
    }
}