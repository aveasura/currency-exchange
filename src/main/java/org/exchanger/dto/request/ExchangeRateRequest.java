package org.exchanger.dto.request;

public record ExchangeRateRequest(
        String baseCurrencyCode,
        String targetCurrencyCode,
        String rate) {
}
