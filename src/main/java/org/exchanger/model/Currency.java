package org.exchanger.model;

public record Currency(
        Long id,
        String fullName,
        String code,
        String sign) {
}
