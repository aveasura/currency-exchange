package org.exchanger.exception;

public abstract class AppException extends RuntimeException {
    private final int status;

    protected AppException(int status, String message) {
        super(message);
        this.status = status;
    }

    protected AppException(int status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
