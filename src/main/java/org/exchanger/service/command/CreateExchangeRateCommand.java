package org.exchanger.service.command;

import java.math.BigDecimal;

public record CreateExchangeRateCommand(
        String baseCurrencyCode,
        String targetCurrencyCode,
        BigDecimal rate
) {
}