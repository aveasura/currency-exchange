package org.exchanger.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigDecimal;

@JsonPropertyOrder({"baseCurrency", "targetCurrency", "rate", "amount", "convertedAmount"})
public record ExchangeResponse(
        CurrencyResponse baseCurrency,
        CurrencyResponse targetCurrency,
        BigDecimal rate,
        BigDecimal amount,
        BigDecimal convertedAmount) {
}