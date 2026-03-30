package org.exchanger.exception;

import jakarta.servlet.http.HttpServletResponse;

public class CurrencyAlreadyExistsException extends AppException {
    public CurrencyAlreadyExistsException(String code) {
        super(HttpServletResponse.SC_CONFLICT, "Currency with code '" + code + "' already exists");
    }
}