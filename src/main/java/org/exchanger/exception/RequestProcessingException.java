package org.exchanger.exception;

import jakarta.servlet.http.HttpServletResponse;

public class RequestProcessingException extends AppException{
    public RequestProcessingException(String message) {
        super(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
    }

    public RequestProcessingException(String message, Throwable cause) {
        super(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message, cause);
    }
}
