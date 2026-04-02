package org.exchanger.repository;

import org.exchanger.config.connection.ConnectionProvider;
import org.exchanger.config.connection.SqliteConnectionProvider;
import org.exchanger.config.database.DatabaseInitializer;
import org.exchanger.model.Currency;
import org.exchanger.repository.jdbc.JdbcCurrencyRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sqlite.SQLiteDataSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JdbcCurrencyRepositoryTest {

    private Path dbFile;
    private JdbcCurrencyRepository repository;

    @BeforeEach
    void setUp() throws IOException {
        dbFile = Files.createTempFile("currency-exchange-test-", ".db");

        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite:" + dbFile);

        ConnectionProvider connectionProvider = new SqliteConnectionProvider(dataSource);
        new DatabaseInitializer(connectionProvider).initializeDatabase();

        repository = new JdbcCurrencyRepository(connectionProvider);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(dbFile);
    }

    @Test
    void shouldFindDefaultCurrencyByCode() {
        Currency currency = repository.findByCode("USD");

        assertAll(
                () -> assertEquals(0L, currency.id()),
                () -> assertEquals("USD", currency.code()),
                () -> assertEquals("United States dollar", currency.fullName()),
                () -> assertEquals("$", currency.sign())
        );
    }

    @Test
    void shouldCreateCurrency() {
        Long id = repository.create("AUD", "Australian dollar", "A$");

        Currency saved = repository.findByCode("AUD");

        assertAll(
                () -> assertEquals(id, saved.id()),
                () -> assertEquals("AUD", saved.code()),
                () -> assertEquals("Australian dollar", saved.fullName()),
                () -> assertEquals("A$", saved.sign())
        );
    }
}