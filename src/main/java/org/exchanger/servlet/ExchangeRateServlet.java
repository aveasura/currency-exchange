package org.exchanger.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.exchanger.dto.request.UpdateExchangeRateRequest;
import org.exchanger.dto.response.ExchangeRateResponse;
import org.exchanger.dto.response.UpdateExchangeRateResponse;
import org.exchanger.service.ExchangeRateService;
import org.exchanger.servlet.parser.CodeParser;

import java.io.IOException;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends AbstractApiServlet {

    private ExchangeRateService exchangeRateService;
    private CodeParser parser;

    @Override
    public void init() {
        super.init();
        exchangeRateService = getService("exchangeRateService", ExchangeRateService.class);
        parser = getService("codeParser", CodeParser.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String currencyPair = parser.parse(request);
        ExchangeRateResponse responseDto = exchangeRateService.get(currencyPair);

        sendJsonResponse(response, responseDto, HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String cleanPath = parser.parse(request);

        String base = parser.extractCode(cleanPath, 0, 3);
        String target = parser.extractCode(cleanPath, 3, 6);
        String rate = parser.extractRate(request);

        UpdateExchangeRateRequest updateRequest = new UpdateExchangeRateRequest(base, target, rate);
        UpdateExchangeRateResponse responseDto = exchangeRateService.patchExchangeRate(updateRequest);

        sendJsonResponse(response, responseDto, HttpServletResponse.SC_OK);
    }
}
