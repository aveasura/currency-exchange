package org.exchanger.servlet.parser;

import jakarta.servlet.http.HttpServletRequest;
import org.exchanger.dto.request.ExchangeRateRequest;

import java.math.BigDecimal;

public class ExchangeRateParser extends AbstractRequestParser<ExchangeRateRequest> {

    private static final String BASE_CURRENCY_CODE_PARAM = "baseCurrencyCode";
    private static final String TARGET_CURRENCY_CODE_PARAM = "targetCurrencyCode";
    private static final String RATE_PARAM = "rate";

    @Override
    public ExchangeRateRequest parse(HttpServletRequest request) {
        String baseCurrencyCode = getRequiredParameter(request, BASE_CURRENCY_CODE_PARAM);
        String targetCurrencyCode = getRequiredParameter(request, TARGET_CURRENCY_CODE_PARAM);
        String rawRate = getRequiredParameter(request, RATE_PARAM);
        BigDecimal rate = parseBigDecimal(rawRate, RATE_PARAM);

        return new ExchangeRateRequest(baseCurrencyCode, targetCurrencyCode, rate);
    }
}
