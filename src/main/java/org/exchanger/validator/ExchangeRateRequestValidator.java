package org.exchanger.validator;

import org.exchanger.dto.request.ExchangeRateRequest;

public class ExchangeRateRequestValidator extends AbstractRequestValidator<ExchangeRateRequest> {
    @Override
    public void validate(ExchangeRateRequest request) {
        validateCodesAndPositiveNumber(request.baseCurrencyCode(), request.targetCurrencyCode(), request.rate());
    }
}
