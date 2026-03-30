package org.exchanger.exception;

import jakarta.servlet.http.HttpServletResponse;

public class ExchangeRateAlreadyExistsException extends AppException {
    public ExchangeRateAlreadyExistsException(String baseCode, String targetCode) {
        super(HttpServletResponse.SC_CONFLICT,
                "Exchange rate for pair '%s' -> '%s' already exists".formatted(baseCode, targetCode));
    }
}