package org.exchanger.mapper;

import org.exchanger.dto.response.CurrencyResponse;
import org.exchanger.dto.response.ExchangeRateResponse;
import org.exchanger.model.Currency;
import org.exchanger.model.ExchangeRate;

public class ExchangeRateMapper implements ResponseMapper<ExchangeRate, ExchangeRateResponse> {

    private final ResponseMapper<Currency, CurrencyResponse> currencyResponseMapper;

    public ExchangeRateMapper(ResponseMapper<Currency, CurrencyResponse> currencyResponseMapper) {
        this.currencyResponseMapper = currencyResponseMapper;
    }

    @Override
    public ExchangeRateResponse toDto(ExchangeRate exchangeRate) {
        CurrencyResponse baseCurrency = currencyResponseMapper.toDto(exchangeRate.getBaseCurrency());
        CurrencyResponse targetCurrency = currencyResponseMapper.toDto(exchangeRate.getTargetCurrency());

        return new ExchangeRateResponse(
                exchangeRate.getId(),
                baseCurrency,
                targetCurrency,
                exchangeRate.getRate()
        );
    }
}
