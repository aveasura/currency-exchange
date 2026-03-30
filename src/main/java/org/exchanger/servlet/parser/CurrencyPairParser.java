package org.exchanger.servlet.parser;

import jakarta.servlet.http.HttpServletRequest;
import org.exchanger.exception.BadRequestException;

public class CurrencyPairParser extends AbstractRequestParser<CurrencyPairRequest> {

    @Override
    public CurrencyPairRequest parse(HttpServletRequest request) {
        String rawPair = getCleanPath(request);
        String pair = normalizeCode(rawPair);

        if (pair.length() != CURRENCY_CODE_LENGTH * 2) {
            throw new BadRequestException("Currency pair should contain 6 letters");
        }

        String base = pair.substring(0, CURRENCY_CODE_LENGTH);
        String target = pair.substring(CURRENCY_CODE_LENGTH);

        return new CurrencyPairRequest(base, target);
    }
}
