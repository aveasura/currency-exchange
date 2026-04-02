package org.exchanger.validator;

import org.exchanger.dto.request.UpdateExchangeRateRequest;

public final class UpdateExchangeRateValidator extends AbstractRequestValidator<UpdateExchangeRateRequest> {
    @Override
    public void validate(UpdateExchangeRateRequest request) {
        validateRate(request.rate());
    }
}
