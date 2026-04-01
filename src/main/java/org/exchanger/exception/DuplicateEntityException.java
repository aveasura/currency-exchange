package org.exchanger.exception;

import jakarta.servlet.http.HttpServletResponse;

public class DuplicateEntityException extends AppException {
    public DuplicateEntityException(String message, Throwable cause) {
        super(HttpServletResponse.SC_CONFLICT, message, cause);
    }
}