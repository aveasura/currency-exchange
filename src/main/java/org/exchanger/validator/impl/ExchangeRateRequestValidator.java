package org.exchanger.validator.impl;

import org.exchanger.dto.request.ExchangeRateRequest;

public final class ExchangeRateRequestValidator extends AbstractRequestValidator<ExchangeRateRequest> {
    @Override
    public void validate(ExchangeRateRequest request) {
        validateCodesAndRate(request.baseCurrencyCode(), request.targetCurrencyCode(), request.rate());
    }
}
