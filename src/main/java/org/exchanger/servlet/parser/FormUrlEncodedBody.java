package org.exchanger.servlet.parser;

import org.exchanger.exception.BadRequestException;

import java.util.Map;

public final class FormUrlEncodedBody {

    private final Map<String, String> params;

    public FormUrlEncodedBody(Map<String, String> params) {
        this.params = params;
    }

    public String getRequired(String name) {
        String value = params.get(name);
        if (value == null || value.isBlank()) {
            throw new BadRequestException("Field '%s' required".formatted(name));
        }
        return value;
    }
}