package org.exchanger.model;

import java.math.BigDecimal;

public record ExchangeRate(
        Long id,
        Currency baseCurrency,
        Currency targetCurrency,
        BigDecimal rate) {
}
