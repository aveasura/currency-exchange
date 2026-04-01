package org.exchanger.mapper;

import org.exchanger.dto.response.CurrencyResponse;
import org.exchanger.dto.response.ExchangeRateResponse;
import org.exchanger.model.Currency;
import org.exchanger.model.ExchangeRate;

public final class ExchangeRateMapper implements ResponseMapper<ExchangeRate, ExchangeRateResponse> {

    private final ResponseMapper<Currency, CurrencyResponse> currencyMapper;

    public ExchangeRateMapper(ResponseMapper<Currency, CurrencyResponse> currencyMapper) {
        this.currencyMapper = currencyMapper;
    }

    @Override
    public ExchangeRateResponse toDto(ExchangeRate exchangeRate) {
        CurrencyResponse baseCurrency = currencyMapper.toDto(exchangeRate.baseCurrency());
        CurrencyResponse targetCurrency = currencyMapper.toDto(exchangeRate.targetCurrency());

        return new ExchangeRateResponse(
                exchangeRate.id(),
                baseCurrency,
                targetCurrency,
                exchangeRate.rate()
        );
    }
}
