package org.exchanger.servlet.parser;

import jakarta.servlet.http.HttpServletRequest;
import org.exchanger.dto.request.UpdateExchangeRateRequest;

public final class UpdateRateParser extends AbstractRequestParser<UpdateExchangeRateRequest> {

    private static final String RATE_PARAM = "rate";

    private final RequestParser<CurrencyPairRequest> codeParser;
    private final FormUrlEncodedBodyParser formBodyParser;

    public UpdateRateParser(RequestParser<CurrencyPairRequest> codeParser) {
        this.codeParser = codeParser;
        this.formBodyParser = new FormUrlEncodedBodyParser();
    }

    @Override
    public UpdateExchangeRateRequest parse(HttpServletRequest request) {
        CurrencyPairRequest codes = codeParser.parse(request);
        FormUrlEncodedBody body = formBodyParser.parse(request);

        String rawRate = body.getRequired(RATE_PARAM);

        return new UpdateExchangeRateRequest(codes.base(), codes.target(), rawRate);
    }
}