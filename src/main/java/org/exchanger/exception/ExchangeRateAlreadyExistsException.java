package org.exchanger.exception;

public final class ExchangeRateAlreadyExistsException extends DuplicateEntityException {
    public ExchangeRateAlreadyExistsException(String baseCode, String targetCode, Throwable cause) {
        super("Exchange rate for pair '%s' -> '%s' already exists".formatted(baseCode, targetCode), cause);
    }
}