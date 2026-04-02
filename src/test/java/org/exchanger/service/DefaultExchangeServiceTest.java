package org.exchanger.service;

import org.exchanger.dto.request.ExchangeRequest;
import org.exchanger.dto.response.CurrencyResponse;
import org.exchanger.dto.response.ExchangeRateResponse;
import org.exchanger.dto.response.ExchangeResponse;
import org.exchanger.exception.InvalidExchangeRequestException;
import org.exchanger.mapper.CurrencyMapper;
import org.exchanger.mapper.ResponseMapper;
import org.exchanger.model.Currency;
import org.exchanger.model.ExchangeRate;
import org.exchanger.repository.CurrencyRepository;
import org.exchanger.repository.ExchangeRateRepository;
import org.exchanger.service.impl.DefaultExchangeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultExchangeServiceTest {

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    private DefaultExchangeService service;

    private final ResponseMapper<Currency, CurrencyResponse> currencyMapper = new CurrencyMapper();

    private final Currency usd = new Currency(0L, "United States Dollar", "USD", "$");
    private final Currency eur = new Currency(1L, "Euro", "EUR", "€");
    private final Currency jpy = new Currency(4L, "Japanese yen", "JPY", "¥");

    @BeforeEach
    void setUp() {
        service = new DefaultExchangeService(currencyRepository, exchangeRateRepository, currencyMapper);
    }

    @Test
    void shouldConvertByDirectRate() {
        when(currencyRepository.findByCode("USD")).thenReturn(usd);
        when(currencyRepository.findByCode("EUR")).thenReturn(eur);

        when(exchangeRateRepository.findByBaseCurrencyIdAndTargetCurrencyId(0L, 1L))
                .thenReturn(Optional.of(new ExchangeRate(100L, usd, eur, new BigDecimal("0.86"))));

        ExchangeResponse result = service.convert(new ExchangeRequest("usd", "eur", "10"));

        assertAll(
                () -> assertEquals("USD", result.baseCurrency().code()),
                () -> assertEquals("EUR", result.targetCurrency().code()),
                () -> assertEquals(new BigDecimal("0.86"), result.rate()),
                () -> assertEquals(new BigDecimal("10"), result.amount()),
                () -> assertEquals(new BigDecimal("8.60"), result.convertedAmount())
        );
    }

    @Test
    void shouldConvertByReverseRate() {
        when(currencyRepository.findByCode("EUR")).thenReturn(eur);
        when(currencyRepository.findByCode("USD")).thenReturn(usd);

        when(exchangeRateRepository.findByBaseCurrencyIdAndTargetCurrencyId(1L, 0L))
                .thenReturn(Optional.empty());

        when(exchangeRateRepository.findByBaseCurrencyIdAndTargetCurrencyId(0L, 1L))
                .thenReturn(Optional.of(new ExchangeRate(101L, usd, eur, new BigDecimal("0.80"))));

        ExchangeResponse result = service.convert(new ExchangeRequest("EUR", "USD", "10"));

        assertAll(
                () -> assertEquals(new BigDecimal("1.250000"), result.rate()),
                () -> assertEquals(new BigDecimal("12.50"), result.convertedAmount())
        );
    }

    @Test
    void shouldConvertByCrossRateThroughUsd() {
        when(currencyRepository.findByCode("EUR")).thenReturn(eur);
        when(currencyRepository.findByCode("JPY")).thenReturn(jpy);
        when(currencyRepository.findByCode("USD")).thenReturn(usd);

        when(exchangeRateRepository.findByBaseCurrencyIdAndTargetCurrencyId(1L, 4L))
                .thenReturn(Optional.empty());
        when(exchangeRateRepository.findByBaseCurrencyIdAndTargetCurrencyId(4L, 1L))
                .thenReturn(Optional.empty());

        when(exchangeRateRepository.findByBaseCurrencyIdAndTargetCurrencyId(0L, 1L))
                .thenReturn(Optional.of(new ExchangeRate(102L, usd, eur, new BigDecimal("0.86"))));

        when(exchangeRateRepository.findByBaseCurrencyIdAndTargetCurrencyId(0L, 4L))
                .thenReturn(Optional.of(new ExchangeRate(103L, usd, jpy, new BigDecimal("149.30"))));

        ExchangeResponse result = service.convert(new ExchangeRequest("EUR", "JPY", "1"));

        assertAll(
                () -> assertEquals(new BigDecimal("173.604651"), result.rate()),
                () -> assertEquals(new BigDecimal("173.60"), result.convertedAmount())
        );
    }

    @Test
    void shouldThrowWhenCurrenciesAreSame() {
        when(currencyRepository.findByCode("USD")).thenReturn(usd);

        InvalidExchangeRequestException exception = assertThrows(
                InvalidExchangeRequestException.class,
                () -> service.convert(new ExchangeRequest("USD", "USD", "10"))
        );

        assertEquals("Same currencies selected", exception.getMessage());
    }
}
