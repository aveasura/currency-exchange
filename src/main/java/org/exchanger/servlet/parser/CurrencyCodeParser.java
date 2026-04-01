package org.exchanger.servlet.parser;

import jakarta.servlet.http.HttpServletRequest;

public final class CurrencyCodeParser extends AbstractRequestParser<String> {
    @Override
    public String parse(HttpServletRequest request) {
        String rawCode = getCleanPath(request);
        return normalizeCode(rawCode);
    }
}
