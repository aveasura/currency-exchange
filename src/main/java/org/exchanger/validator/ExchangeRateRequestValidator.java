package org.exchanger.validator;

import org.exchanger.dto.request.ExchangeRateRequest;

public class ExchangeRateRequestValidator extends AbstractRequestValidator<ExchangeRateRequest> {
    @Override
    public void validate(ExchangeRateRequest request) {
        String baseCurrencyCode = request.baseCurrencyCode();
        String targetCurrencyCode = request.targetCurrencyCode();
        String rawRate = request.rate();

        validateCode(baseCurrencyCode);
        validateCode(targetCurrencyCode);
        validateNumericValue(rawRate);
    }
}
