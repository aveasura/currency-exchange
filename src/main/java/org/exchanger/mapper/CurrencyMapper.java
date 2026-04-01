package org.exchanger.mapper;

import org.exchanger.dto.response.CurrencyResponse;
import org.exchanger.model.Currency;

public final class CurrencyMapper implements
        ResponseMapper<Currency, CurrencyResponse> {

    @Override
    public CurrencyResponse toDto(Currency currency) {
        return new CurrencyResponse(
                currency.id(),
                currency.fullName(),
                currency.code(),
                currency.sign());
    }
}
