package org.exchanger.exception;

public final class CurrencyAlreadyExistsException extends ConflictException {
    public CurrencyAlreadyExistsException(String code, Throwable cause) {
        super("Currency with code '%s' already exists".formatted(code), cause);
    }
}