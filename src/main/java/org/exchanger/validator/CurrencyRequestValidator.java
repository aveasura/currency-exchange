package org.exchanger.validator;

import org.exchanger.dto.request.CurrencyRequest;

public class CurrencyRequestValidator extends AbstractRequestValidator<CurrencyRequest> {
    @Override
    public void validate(CurrencyRequest request) {
        validateCode(request.code());
    }
}
