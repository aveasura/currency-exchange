package org.exchanger.validator;

import org.exchanger.dto.request.UpdateExchangeRateRequest;

public class UpdateExchangeRateValidator extends AbstractRequestValidator<UpdateExchangeRateRequest> {
    @Override
    public void validate(UpdateExchangeRateRequest request) {
        validateCodesAndPositiveNumber(request.baseCurrencyCode(), request.targetCurrencyCode(), request.rate());
    }
}
