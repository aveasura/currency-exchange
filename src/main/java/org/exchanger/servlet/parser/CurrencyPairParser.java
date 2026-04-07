package org.exchanger.servlet.parser;

import jakarta.servlet.http.HttpServletRequest;
import org.exchanger.exception.ValidationException;

public final class CurrencyPairParser extends AbstractRequestParser<CurrencyPairRequest> {

    private static final String CURRENCY_PAIR_PATTERN = "[A-Z]{6}";

    @Override
    public CurrencyPairRequest parse(HttpServletRequest request) {
        String rawPair = getCleanPath(request);
        String pair = normalizeCode(rawPair);

        if (!pair.matches(CURRENCY_PAIR_PATTERN)) {
            throw new ValidationException("Currency pair should contain exactly 6 latin letters");
        }

        String base = pair.substring(0, CURRENCY_CODE_LENGTH);
        String target = pair.substring(CURRENCY_CODE_LENGTH);

        return new CurrencyPairRequest(base, target);
    }
}
