package org.exchanger.mapper;

import org.exchanger.dto.request.CurrencyRequest;
import org.exchanger.dto.response.CurrencyResponse;
import org.exchanger.model.Currency;

public final class CurrencyMapper implements
        ResponseMapper<Currency, CurrencyResponse>,
        RequestMapper<CurrencyRequest, Currency> {

    @Override
    public CurrencyResponse toDto(Currency currency) {
        return new CurrencyResponse(
                currency.getId(),
                currency.getFullName(),
                currency.getCode(),
                currency.getSign());
    }

    @Override
    public Currency toEntity(CurrencyRequest dto) {
        return new Currency(
                dto.name(),
                dto.code(),
                dto.sign()
        );
    }
}
