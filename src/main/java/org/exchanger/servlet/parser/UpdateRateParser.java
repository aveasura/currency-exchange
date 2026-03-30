package org.exchanger.servlet.parser;

import jakarta.servlet.http.HttpServletRequest;
import org.exchanger.dto.request.UpdateExchangeRateRequest;
import org.exchanger.exception.BadRequestException;

import java.io.IOException;

public class UpdateRateParser extends AbstractRequestParser<UpdateExchangeRateRequest> {

    private static final String RATE_PARAM = "rate";
    private static final String RATE_PREFIX = RATE_PARAM + "=";

    private final RequestParser<CurrencyPairRequest> codeParser;

    public UpdateRateParser(RequestParser<CurrencyPairRequest> codeParser) {
        this.codeParser = codeParser;
    }

    @Override
    public UpdateExchangeRateRequest parse(HttpServletRequest request) {
        CurrencyPairRequest codes = codeParser.parse(request);
        String rawRate = extractRate(request);

        return new UpdateExchangeRateRequest(codes.base(), codes.target(), rawRate);
    }

    private String extractRate(HttpServletRequest request) {
        String body = null;
        try {
            body = request.getReader().readLine();
        } catch (IOException e) {
            // todo
            throw new BadRequestException("Failed to read request body");
        }
        if (body == null || !body.startsWith(RATE_PREFIX)) {
            throw new BadRequestException("Field 'rate' required");
        }
        return java.net.URLDecoder.decode(
                body.substring(RATE_PREFIX.length()),
                java.nio.charset.StandardCharsets.UTF_8
        );
    }
}
