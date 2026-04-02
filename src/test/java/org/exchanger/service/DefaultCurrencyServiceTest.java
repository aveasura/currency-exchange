package org.exchanger.service;

import org.exchanger.dto.request.CurrencyRequest;
import org.exchanger.dto.response.CurrencyResponse;
import org.exchanger.exception.CurrencyAlreadyExistsException;
import org.exchanger.exception.CurrencyNotFoundException;
import org.exchanger.exception.DuplicateEntityException;
import org.exchanger.mapper.CurrencyMapper;
import org.exchanger.mapper.ResponseMapper;
import org.exchanger.model.Currency;
import org.exchanger.repository.CurrencyRepository;
import org.exchanger.service.impl.DefaultCurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultCurrencyServiceTest {

    @Mock
    private CurrencyRepository currencyRepository;

    private DefaultCurrencyService service;

    private final ResponseMapper<Currency, CurrencyResponse> currencyMapper = new CurrencyMapper();

    private final Currency usd = new Currency(0L, "United States dollar", "USD", "$");
    private final Currency eur = new Currency(1L, "Euro", "EUR", "€");

    @BeforeEach
    void setUp() {
        service = new DefaultCurrencyService(currencyRepository, currencyMapper);
    }

    @Test
    void shouldCreateCurrency() {
        CurrencyRequest request = new CurrencyRequest("Australian dollar", "AUD", "A$");

        when(currencyRepository.create("AUD", "Australian dollar", "A$"))
                .thenReturn(5L);

        CurrencyResponse result = service.create(request);

        assertAll(
                () -> assertEquals(5L, result.id()),
                () -> assertEquals("Australian dollar", result.name()),
                () -> assertEquals("AUD", result.code()),
                () -> assertEquals("A$", result.sign())
        );
    }

    @Test
    void shouldThrowWhenCurrencyAlreadyExists() {
        CurrencyRequest request = new CurrencyRequest("Australian dollar", "AUD", "A$");

        when(currencyRepository.create("AUD", "Australian dollar", "A$"))
                .thenThrow(new DuplicateEntityException("duplicate", null));

        CurrencyAlreadyExistsException exception = assertThrows(
                CurrencyAlreadyExistsException.class,
                () -> service.create(request)
        );

        assertEquals("Currency with code 'AUD' already exists", exception.getMessage());
    }

    @Test
    void shouldReturnCurrencyByCode() {
        when(currencyRepository.findByCode("USD")).thenReturn(usd);

        CurrencyResponse result = service.getByCurrencyCode("USD");

        assertAll(
                () -> assertEquals(0L, result.id()),
                () -> assertEquals("United States dollar", result.name()),
                () -> assertEquals("USD", result.code()),
                () -> assertEquals("$", result.sign())
        );

        verify(currencyRepository).findByCode("USD");
    }

    @Test
    void shouldThrowWhenCurrencyNotFound() {
        when(currencyRepository.findByCode("GBP"))
                .thenThrow(new CurrencyNotFoundException("GBP"));

        CurrencyNotFoundException exception = assertThrows(
                CurrencyNotFoundException.class,
                () -> service.getByCurrencyCode("GBP")
        );

        assertEquals("Currency with code 'GBP' not found", exception.getMessage());
    }

    @Test
    void shouldReturnAllCurrencies() {
        when(currencyRepository.findAll()).thenReturn(List.of(usd, eur));

        List<CurrencyResponse> result = service.getAll();

        assertEquals(
                List.of(
                        new CurrencyResponse(0L, "United States dollar", "USD", "$"),
                        new CurrencyResponse(1L, "Euro", "EUR", "€")
                ),
                result
        );
    }
}
