package org.exchanger.exception;

public final class InvalidExchangeRequestException extends BadRequestException {
    public InvalidExchangeRequestException(String message) {
        super(message);
    }
}
