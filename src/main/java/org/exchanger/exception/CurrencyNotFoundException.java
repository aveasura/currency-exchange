package org.exchanger.exception;

public class CurrencyNotFoundException extends NotFoundException {
    public CurrencyNotFoundException(String code) {
        super("Currency with code '%s' not found".formatted(code));
    }
}