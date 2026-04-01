package org.exchanger.exception;

public class InvalidExchangeRequestException extends BadRequestException {
    public InvalidExchangeRequestException(String message) {
        super(message);
    }
}
