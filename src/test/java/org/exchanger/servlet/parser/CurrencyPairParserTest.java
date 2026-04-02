package org.exchanger.servlet.parser;

import jakarta.servlet.http.HttpServletRequest;
import org.exchanger.exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyPairParserTest {

    @Mock
    private HttpServletRequest request;

    private CurrencyPairParser parser;

    @BeforeEach
    void setUp() {
        parser = new CurrencyPairParser();
    }

    @Test
    void shouldParseCurrencyPairFromPath() {
        when(request.getPathInfo()).thenReturn("/usdeur");

        CurrencyPairRequest result = parser.parse(request);

        assertAll(
                () -> assertEquals("USD", result.base()),
                () -> assertEquals("EUR", result.target())
        );
    }

    @Test
    void shouldThrowWhenPathIsMissing() {
        when(request.getPathInfo()).thenReturn(null);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> parser.parse(request)
        );

        assertEquals("Path variable is missing", exception.getMessage());
    }

    @Test
    void shouldThrowWhenCurrencyPairFormatIsInvalid() {
        when(request.getPathInfo()).thenReturn("/USD12R");

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> parser.parse(request)
        );

        assertEquals("Currency pair should contain exactly 6 latin letters", exception.getMessage());
    }
}
