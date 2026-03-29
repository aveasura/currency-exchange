package org.exchanger.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.exchanger.dto.ErrorResponse;
import org.exchanger.dto.request.ExchangeRateRequest;
import org.exchanger.dto.response.ExchangeRateResponse;
import org.exchanger.exception.AppException;
import org.exchanger.service.ExchangeRateService;

import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends AbstractApiServlet {

    private ExchangeRateService exchangeRateService;

    @Override
    public void init() {
        super.init();
        exchangeRateService = getService("exchangeRateService", ExchangeRateService.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            List<ExchangeRateResponse> exchangeRates = exchangeRateService.getAll();
            sendResponse(response, exchangeRates, HttpServletResponse.SC_OK);
        } catch (AppException e) {
            sendResponse(response, new ErrorResponse(e.getMessage()), e.getStatus());
        }
    }

    // todo parser ExchangeRateRequest
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String baseCurrencyCode = request.getParameter("baseCurrencyCode");
        String targetCurrencyCode = request.getParameter("targetCurrencyCode");
        String rate = request.getParameter("rate");

        ExchangeRateRequest requestDto = new ExchangeRateRequest(
                baseCurrencyCode,
                targetCurrencyCode,
                rate
        );

        ExchangeRateResponse responseDto = exchangeRateService.addExchangeRate(requestDto);

        sendResponse(response, responseDto, HttpServletResponse.SC_CREATED);
    }
}
