package org.exchanger.exception;

import jakarta.servlet.http.HttpServletResponse;

public class NotFoundException extends AppException {
    protected NotFoundException(String message) {
        super(HttpServletResponse.SC_NOT_FOUND, message);
    }
}
