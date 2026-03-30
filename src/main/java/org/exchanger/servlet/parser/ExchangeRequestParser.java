package org.exchanger.servlet.parser;

import jakarta.servlet.http.HttpServletRequest;
import org.exchanger.dto.request.ExchangeRequest;

import java.math.BigDecimal;

public class ExchangeRequestParser extends AbstractRequestParser<ExchangeRequest> {

    private static final String FROM_PARAM = "from";
    private static final String TO_PARAM = "to";
    private static final String AMOUNT_PARAM = "amount";

    @Override
    public ExchangeRequest parse(HttpServletRequest request) {
        String rawFrom = getRequiredParameter(request, FROM_PARAM);
        String rawTo = getRequiredParameter(request, TO_PARAM);
        String rawAmount = getRequiredParameter(request, AMOUNT_PARAM);

        String from = normalizeCode(rawFrom);
        String to = normalizeCode(rawTo);
        BigDecimal amount = parseBigDecimal(rawAmount, AMOUNT_PARAM);

        return new ExchangeRequest(from, to, amount);
    }
}
