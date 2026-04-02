package org.exchanger.service.command;

import java.math.BigDecimal;

public record UpdateExchangeRateCommand(
        String baseCurrencyCode,
        String targetCurrencyCode,
        BigDecimal rate
) {
}