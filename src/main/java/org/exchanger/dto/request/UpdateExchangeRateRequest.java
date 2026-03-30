package org.exchanger.dto.request;

public record UpdateExchangeRateRequest(
        String baseCurrencyCode,
        String targetCurrencyCode,
        String rate
) {
}
