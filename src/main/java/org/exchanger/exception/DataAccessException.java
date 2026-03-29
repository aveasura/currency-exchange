package org.exchanger.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DataAccessException extends AppException {
    public DataAccessException(String message) {
        super(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
        initCause(cause);
    }
}
