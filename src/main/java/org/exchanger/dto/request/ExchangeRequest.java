package org.exchanger.dto.request;

import java.math.BigDecimal;

public record ExchangeRequest(
        String from,
        String to,
        BigDecimal amount) {
}
