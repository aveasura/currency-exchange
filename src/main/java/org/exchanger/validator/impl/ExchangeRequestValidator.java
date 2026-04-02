package org.exchanger.validator.impl;

import org.exchanger.dto.request.ExchangeRequest;

public final class ExchangeRequestValidator extends AbstractRequestValidator<ExchangeRequest> {
    @Override
    public void validate(ExchangeRequest request) {
        validateCodesAndAmount(request.from(), request.to(), request.amount());
    }
}
