package org.exchanger.dto.request;

import java.math.BigDecimal;

public record UpdateExchangeRateRequest(
        String baseCurrencyCode,
        String targetCurrencyCode,
        BigDecimal rate
) {
}
