package org.exchanger.exception;

public final class ExchangeRateNotFoundException extends NotFoundException {
    public ExchangeRateNotFoundException(String baseCode, String targetCode) {
        super("Exchange rate for %s -> %s not found".formatted(baseCode, targetCode));
    }
}
