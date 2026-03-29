package org.exchanger.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.exchanger.dto.ErrorResponse;
import org.exchanger.dto.request.UpdateExchangeRateRequest;
import org.exchanger.dto.response.ExchangeRateResponse;
import org.exchanger.dto.response.UpdateExchangeRateResponse;
import org.exchanger.exception.AppException;
import org.exchanger.exception.BadRequestException;
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

    // todo parser ExchangeRateRequest
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            String cleanPath = parser.getCleanPath(request);
            // todo validate
            if (cleanPath == null || cleanPath.length() != 6) {
                throw new BadRequestException("Currency pair codes required. Example: USDEUR");
            }

            String baseCurrencyCode = parser.extractCode(cleanPath, 0, 3);
            String targetCurrencyCode = parser.extractCode(cleanPath, 3, 6);

            ExchangeRateResponse responseDto = exchangeRateService.get(baseCurrencyCode, targetCurrencyCode);

            sendResponse(response, responseDto, HttpServletResponse.SC_OK);
        } catch (AppException e) {
            sendResponse(response, new ErrorResponse(e.getMessage()), e.getStatus());
        }
    }

    // todo parser ExchangeRateRequest
    @Override
    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String cleanPath = parser.getCleanPath(request);

        String base = parser.extractCode(cleanPath, 0, 3);
        String target = parser.extractCode(cleanPath, 3, 6);
        String rate = parser.extractRate(request);

        UpdateExchangeRateRequest updateRequest = new UpdateExchangeRateRequest(base, target, rate);
        UpdateExchangeRateResponse responseDto = exchangeRateService.patchExchangeRate(updateRequest);

        sendResponse(response, responseDto, HttpServletResponse.SC_OK);
    }
}
