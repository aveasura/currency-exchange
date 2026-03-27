package org.exchanger.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.exchanger.dto.response.ExchangeRateResponse;
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
        List<ExchangeRateResponse> exchangeRates = exchangeRateService.getAll();
        sendJsonResponse(response, exchangeRates, HttpServletResponse.SC_OK);
    }
}
