package org.exchanger.validator;

import org.exchanger.dto.request.CurrencyRequest;
import org.exchanger.exception.BadRequestException;

public class CurrencyRequestValidator extends AbstractRequestValidator<CurrencyRequest> {
    @Override
    public void validate(CurrencyRequest request) {
        validateName(request.name());
        validateSign(request.sign());
        validateCode(request.code());
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new BadRequestException("Field 'name' required");
        }

        if (name.length() > 50) {
            throw new BadRequestException("Maximum 'name' size exceeded");
        }
    }

    private void validateSign(String sign) {
        if (sign == null || sign.isBlank()) {
            throw new BadRequestException("Field 'sign' required");
        }

        if (sign.length() > 10) {
            throw new BadRequestException("Maximum 'sign' size exceeded");
        }
    }
}
