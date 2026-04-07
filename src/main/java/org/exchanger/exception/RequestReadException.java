package org.exchanger.exception;

public class RequestReadException extends InfrastructureException {
    public RequestReadException(String message, Throwable cause) {
        super(message, cause);
    }
}