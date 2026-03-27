package org.exchanger.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "name", "code", "sign"})
public record CurrencyResponse(
        Long id,
        String name,
        String code,
        String sign) {
}
