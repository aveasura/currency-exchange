package org.exchanger.exception;

import jakarta.servlet.http.HttpServletResponse;

public class ResponseProcessingException extends AppException {
    public ResponseProcessingException(String message, Throwable cause) {
        super(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message, cause);
    }
}