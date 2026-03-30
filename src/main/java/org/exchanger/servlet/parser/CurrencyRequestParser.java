package org.exchanger.servlet.parser;

import jakarta.servlet.http.HttpServletRequest;
import org.exchanger.dto.request.CurrencyRequest;
import org.exchanger.exception.BadRequestException;

public class CurrencyRequestParser extends AbstractRequestParser<CurrencyRequest> {

    private static final String INVALID_CODE_MESSAGE =
            "Currency code should contain exactly 3 latin letters. Example: EUR";

    private static final String NAME_PARAM = "name";
    private static final String CODE_PARAM = "code";
    private static final String SIGN_PARAM = "sign";

    @Override
    public CurrencyRequest parse(HttpServletRequest request) {
        String name = getRequiredParameter(request, NAME_PARAM);
        String rawCode = getRequiredParameter(request, CODE_PARAM);
        String sign = getRequiredParameter(request, SIGN_PARAM);

        String code = normalizeCode(rawCode);

        validateCurrencyCode(code);

        return new CurrencyRequest(name, code, sign);
    }

    // todo validator
    private void validateCurrencyCode(String code) {
        if (!code.matches("[A-Z]{3}")) {
            throw new BadRequestException(INVALID_CODE_MESSAGE);
        }
    }
}
