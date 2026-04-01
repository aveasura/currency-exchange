package org.exchanger.exception;

public final class CurrencyNotFoundException extends NotFoundException {
    public CurrencyNotFoundException(String code) {
        super("Currency with code '%s' not found".formatted(code));
    }
}