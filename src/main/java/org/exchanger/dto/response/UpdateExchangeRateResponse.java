package org.exchanger.dto.response;

import java.math.BigDecimal;

public record UpdateExchangeRateResponse (
        Long id,
        CurrencyResponse baseCurrency,
        CurrencyResponse targetCurrency,
        BigDecimal rate
){
}
