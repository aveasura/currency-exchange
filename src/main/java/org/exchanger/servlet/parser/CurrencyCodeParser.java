package org.exchanger.servlet.parser;

import jakarta.servlet.http.HttpServletRequest;
import org.exchanger.exception.BadRequestException;

public class CurrencyCodeParser extends AbstractRequestParser<String> {

    private static final String INVALID_CODE_MESSAGE =
            "Currency code should contain 3 letters. ex: EUR";

    @Override
    public String parse(HttpServletRequest request) {
        String rawCode = getCleanPath(request);
        String code = normalizeCode(rawCode);

        if (code.length() != CURRENCY_CODE_LENGTH) {
            throw new BadRequestException(INVALID_CODE_MESSAGE);
        }

        return code;
    }
}
