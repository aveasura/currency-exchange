package org.exchanger.service;

import org.exchanger.dto.response.CurrencyResponse;
import org.exchanger.dto.response.ExchangeRateResponse;
import org.exchanger.dto.response.UpdateExchangeRateResponse;
import org.exchanger.exception.CurrencyNotFoundException;
import org.exchanger.exception.DuplicateEntityException;
import org.exchanger.exception.ExchangeRateAlreadyExistsException;
import org.exchanger.exception.ExchangeRateNotFoundException;
import org.exchanger.mapper.CurrencyMapper;
import org.exchanger.mapper.ExchangeRateMapper;
import org.exchanger.mapper.ResponseMapper;
import org.exchanger.mapper.UpdateExchangeRateMapper;
import org.exchanger.model.Currency;
import org.exchanger.model.ExchangeRate;
import org.exchanger.repository.CurrencyRepository;
import org.exchanger.repository.ExchangeRateRepository;
import org.exchanger.service.command.CreateExchangeRateCommand;
import org.exchanger.service.command.UpdateExchangeRateCommand;
import org.exchanger.service.impl.DefaultExchangeRateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultExchangeRateServiceTest {

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    private DefaultExchangeRateService service;

    private final ResponseMapper<Currency, CurrencyResponse> currencyMapper = new CurrencyMapper();
    private final ResponseMapper<ExchangeRate, ExchangeRateResponse> exchangeRateMapper =
            new ExchangeRateMapper(currencyMapper);
    private final ResponseMapper<ExchangeRate, UpdateExchangeRateResponse> updateExchangeRateMapper =
            new UpdateExchangeRateMapper(exchangeRateMapper);

    private final Currency usd = new Currency(0L, "United States dollar", "USD", "$");
    private final Currency eur = new Currency(1L, "Euro", "EUR", "€");
    private final Currency jpy = new Currency(2L, "Japanese yen", "JPY", "¥");

    @BeforeEach
    void setUp() {
        service = new DefaultExchangeRateService(
                currencyRepository,
                exchangeRateRepository,
                exchangeRateMapper,
                updateExchangeRateMapper
        );
    }

    @Test
    void shouldReturnAllExchangeRates() {
        ExchangeRate usdToEur = new ExchangeRate(0L, usd, eur, new BigDecimal("0.86"));
        ExchangeRate usdToJpy = new ExchangeRate(1L, usd, jpy, new BigDecimal("149.30"));

        when(exchangeRateRepository.findAll()).thenReturn(List.of(usdToEur, usdToJpy));

        List<ExchangeRateResponse> result = service.getAll();

        assertEquals(
                List.of(
                        new ExchangeRateResponse(
                                0L,
                                new CurrencyResponse(0L, "United States dollar", "USD", "$"),
                                new CurrencyResponse(1L, "Euro", "EUR", "€"),
                                new BigDecimal("0.86")
                        ),
                        new ExchangeRateResponse(
                                1L,
                                new CurrencyResponse(0L, "United States dollar", "USD", "$"),
                                new CurrencyResponse(2L, "Japanese yen", "JPY", "¥"),
                                new BigDecimal("149.30")
                        )
                ),
                result
        );
    }

    @Test
    void shouldReturnExchangeRateByCurrencyPair() {
        ExchangeRate exchangeRate = new ExchangeRate(0L, usd, eur, new BigDecimal("0.86"));

        when(currencyRepository.findByCode("USD")).thenReturn(usd);
        when(currencyRepository.findByCode("EUR")).thenReturn(eur);
        when(exchangeRateRepository.findByBaseCurrencyIdAndTargetCurrencyId(0L, 1L))
                .thenReturn(Optional.of(exchangeRate));

        ExchangeRateResponse result = service.get("USD", "EUR");

        assertAll(
                () -> assertEquals(0L, result.id()),
                () -> assertEquals("USD", result.baseCurrency().code()),
                () -> assertEquals("EUR", result.targetCurrency().code()),
                () -> assertEquals(new BigDecimal("0.86"), result.rate())
        );

        verify(currencyRepository).findByCode("USD");
        verify(currencyRepository).findByCode("EUR");
    }

    @Test
    void shouldThrowWhenExchangeRateNotFound() {
        when(currencyRepository.findByCode("USD")).thenReturn(usd);
        when(currencyRepository.findByCode("EUR")).thenReturn(eur);
        when(exchangeRateRepository.findByBaseCurrencyIdAndTargetCurrencyId(0L, 1L))
                .thenReturn(Optional.empty());

        ExchangeRateNotFoundException exception = assertThrows(
                ExchangeRateNotFoundException.class,
                () -> service.get("USD", "EUR")
        );

        assertEquals("Exchange rate for USD -> EUR not found", exception.getMessage());
    }

    @Test
    void shouldCreateExchangeRate() {
        CreateExchangeRateCommand command =
                new CreateExchangeRateCommand("USD", "EUR", new BigDecimal("0.86"));

        when(currencyRepository.findByCode("USD")).thenReturn(usd);
        when(currencyRepository.findByCode("EUR")).thenReturn(eur);
        when(exchangeRateRepository.create(0L, 1L, new BigDecimal("0.86")))
                .thenReturn(10L);

        ExchangeRateResponse result = service.create(command);

        assertAll(
                () -> assertEquals(10L, result.id()),
                () -> assertEquals("USD", result.baseCurrency().code()),
                () -> assertEquals("EUR", result.targetCurrency().code()),
                () -> assertEquals(new BigDecimal("0.86"), result.rate())
        );
    }

    @Test
    void shouldThrowWhenExchangeRateAlreadyExists() {
        CreateExchangeRateCommand command =
                new CreateExchangeRateCommand("USD", "EUR", new BigDecimal("0.86"));

        when(currencyRepository.findByCode("USD")).thenReturn(usd);
        when(currencyRepository.findByCode("EUR")).thenReturn(eur);
        when(exchangeRateRepository.create(0L, 1L, new BigDecimal("0.86")))
                .thenThrow(new DuplicateEntityException("duplicate", null));

        ExchangeRateAlreadyExistsException exception = assertThrows(
                ExchangeRateAlreadyExistsException.class,
                () -> service.create(command)
        );

        assertEquals(
                "Exchange rate for pair 'USD' -> 'EUR' already exists",
                exception.getMessage()
        );
    }

    @Test
    void shouldUpdateExchangeRate() {
        UpdateExchangeRateCommand command =
                new UpdateExchangeRateCommand("USD", "EUR", new BigDecimal("0.91"));
        ExchangeRate existingExchangeRate = new ExchangeRate(7L, usd, eur, new BigDecimal("0.86"));

        when(currencyRepository.findByCode("USD")).thenReturn(usd);
        when(currencyRepository.findByCode("EUR")).thenReturn(eur);
        when(exchangeRateRepository.findByBaseCurrencyIdAndTargetCurrencyId(0L, 1L))
                .thenReturn(Optional.of(existingExchangeRate));

        UpdateExchangeRateResponse result = service.updateExchangeRate(command);

        verify(exchangeRateRepository).updateRateById(7L, new BigDecimal("0.91"));

        assertAll(
                () -> assertEquals(7L, result.id()),
                () -> assertEquals("USD", result.baseCurrency().code()),
                () -> assertEquals("EUR", result.targetCurrency().code()),
                () -> assertEquals(new BigDecimal("0.91"), result.rate())
        );
    }

    @Test
    void shouldThrowWhenUpdatingMissingExchangeRate() {
        UpdateExchangeRateCommand command =
                new UpdateExchangeRateCommand("USD", "EUR", new BigDecimal("0.91"));

        when(currencyRepository.findByCode("USD")).thenReturn(usd);
        when(currencyRepository.findByCode("EUR")).thenReturn(eur);
        when(exchangeRateRepository.findByBaseCurrencyIdAndTargetCurrencyId(0L, 1L))
                .thenReturn(Optional.empty());

        ExchangeRateNotFoundException exception = assertThrows(
                ExchangeRateNotFoundException.class,
                () -> service.updateExchangeRate(command)
        );

        assertEquals("Exchange rate for USD -> EUR not found", exception.getMessage());
        verify(exchangeRateRepository, never()).updateRateById(any(), any());
    }

    @Test
    void shouldThrowExchangeRateNotFoundWhenBaseCurrencyDoesNotExist() {
        when(currencyRepository.findByCode("UQK"))
                .thenThrow(new CurrencyNotFoundException("UQK"));

        ExchangeRateNotFoundException exception = assertThrows(
                ExchangeRateNotFoundException.class,
                () -> service.get("UQK", "VWM")
        );

        assertEquals("Exchange rate for UQK -> VWM not found", exception.getMessage());
    }
}
