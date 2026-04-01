package org.exchanger.servlet.parser;

import jakarta.servlet.http.HttpServletRequest;
import org.exchanger.dto.request.UpdateExchangeRateRequest;
import org.exchanger.exception.BadRequestException;
import org.exchanger.exception.RequestProcessingException;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class UpdateRateParser extends AbstractRequestParser<UpdateExchangeRateRequest> {

    private static final String RATE_PARAM = "rate";

    private final RequestParser<CurrencyPairRequest> codeParser;

    public UpdateRateParser(RequestParser<CurrencyPairRequest> codeParser) {
        this.codeParser = codeParser;
    }

    @Override
    public UpdateExchangeRateRequest parse(HttpServletRequest request) {
        CurrencyPairRequest codes = codeParser.parse(request);
        Map<String, String> params = parseFormBody(request);

        String rawRate = params.get(RATE_PARAM);
        if (rawRate == null || rawRate.isBlank()) {
            throw new BadRequestException("Field 'rate' required");
        }

        return new UpdateExchangeRateRequest(codes.base(), codes.target(), rawRate);
    }

    private Map<String, String> parseFormBody(HttpServletRequest request) {
        String body;
        try {
            body = request.getReader().readLine();
        } catch (IOException e) {
            throw new RequestProcessingException("Failed to read request body", e);
        }

        if (body == null || body.isBlank()) {
            throw new BadRequestException("Request body is empty");
        }

        Map<String, String> params = new HashMap<>();

        for (String pair : body.split("&")) {
            String[] parts = pair.split("=", 2);

            String key = decode(parts[0]);
            String value = parts.length > 1 ? decode(parts[1]) : "";

            params.put(key, value);
        }

        return params;
    }

    private String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}