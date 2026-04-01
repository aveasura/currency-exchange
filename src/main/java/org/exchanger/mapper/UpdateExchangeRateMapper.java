package org.exchanger.mapper;

import org.exchanger.dto.response.ExchangeRateResponse;
import org.exchanger.dto.response.UpdateExchangeRateResponse;
import org.exchanger.model.ExchangeRate;

public final class UpdateExchangeRateMapper implements ResponseMapper<ExchangeRate, UpdateExchangeRateResponse> {

    private final ResponseMapper<ExchangeRate, ExchangeRateResponse> mapper;

    public UpdateExchangeRateMapper(ResponseMapper<ExchangeRate, ExchangeRateResponse> mapper) {
        this.mapper = mapper;
    }

    @Override
    public UpdateExchangeRateResponse toDto(ExchangeRate exchangeRate) {
        ExchangeRateResponse dto = mapper.toDto(exchangeRate);
        return new UpdateExchangeRateResponse(
                dto.id(),
                dto.baseCurrency(),
                dto.targetCurrency(),
                dto.rate()
        );
    }
}
