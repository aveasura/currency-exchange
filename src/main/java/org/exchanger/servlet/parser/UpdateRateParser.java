package org.exchanger.servlet.parser;

import jakarta.servlet.http.HttpServletRequest;
import org.exchanger.dto.request.UpdateExchangeRateRequest;
import org.exchanger.exception.BadRequestException;

import java.util.Map;

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
        Map<String, String> params = formBodyParser.parse(request);

        String rawRate = params.get(RATE_PARAM);
        if (rawRate == null || rawRate.isBlank()) {
            throw new BadRequestException("Field 'rate' required");
        }

        return new UpdateExchangeRateRequest(codes.base(), codes.target(), rawRate);
    }
}