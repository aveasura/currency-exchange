package org.exchanger.exception;

public final class CurrencyNotFoundException extends EntityNotFoundException {
    public CurrencyNotFoundException(String code) {
        super("Currency with code '%s' not found".formatted(code));
    }
}