package org.exchanger.exception;

import jakarta.servlet.http.HttpServletResponse;

public class BadRequestException extends AppException{
    public BadRequestException(String message) {
        super(HttpServletResponse.SC_BAD_REQUEST, message);
    }
}
