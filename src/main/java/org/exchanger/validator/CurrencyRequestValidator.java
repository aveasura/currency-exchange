package org.exchanger.validator;

import org.exchanger.dto.request.CurrencyRequest;
import org.exchanger.exception.BadRequestException;

public final class CurrencyRequestValidator extends AbstractRequestValidator<CurrencyRequest> {

    private static final int MAX_NAME_LENGTH = 50;
    private static final int MAX_SIGN_LENGTH = 5;

    @Override
    public void validate(CurrencyRequest request) {
        validateName(request.name());
        validateCurrencyCode(request.code());
        validateSign(request.sign());
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new BadRequestException("Field 'name' required");
        }

        if (name.trim().length() > MAX_NAME_LENGTH) {
            throw new BadRequestException("Currency name should not exceed 50 characters");
        }
    }

    private void validateCurrencyCode(String code) {
        if (code == null || code.isBlank()) {
            throw new BadRequestException("Field 'code' required");
        }

        validateCode(code.trim().toUpperCase());
    }

    private void validateSign(String sign) {
        if (sign == null || sign.isBlank()) {
            throw new BadRequestException("Field 'sign' required");
        }

        if (sign.trim().length() > MAX_SIGN_LENGTH) {
            throw new BadRequestException("Currency sign should not exceed 5 characters");
        }
    }
}