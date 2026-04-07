package org.exchanger.exception;

public final class InvalidExchangeRequestException extends ValidationException {
    public InvalidExchangeRequestException(String message) {
        super(message);
    }
}
