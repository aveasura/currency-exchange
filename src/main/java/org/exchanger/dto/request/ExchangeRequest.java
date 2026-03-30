package org.exchanger.dto.request;

public record ExchangeRequest(
        String from,
        String to,
        String amount) {
}
