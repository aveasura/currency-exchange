package org.exchanger.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigDecimal;

@JsonPropertyOrder({"id", "baseCurrency", "targetCurrency", "rate"})
public record ExchangeRateResponse(
        Long id,
        CreateCurrencyResponse baseCurrency,
        CreateCurrencyResponse targetCurrency,
        BigDecimal rate
) {
}
