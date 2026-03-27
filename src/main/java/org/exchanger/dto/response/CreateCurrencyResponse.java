package org.exchanger.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "name", "code", "sign"})
public record CreateCurrencyResponse(Long id, String name, String code, String sing) {
}
