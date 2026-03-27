package org.exchanger.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.exchanger.dto.response.ExchangeRateResponse;
import org.exchanger.service.ExchangeRateService;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends BaseServlet {

    private ExchangeRateService exchangeRateService;

    @Override
    public void init() {
        super.init();
        exchangeRateService = getService("exchangeRateService", ExchangeRateService.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        // todo path parser
        String pathInfo = request.getPathInfo();
        String pair = pathInfo.substring(1);

        ExchangeRateResponse exchangeRate = exchangeRateService.get(pair);
        sendJsonResponse(response, exchangeRate, HttpServletResponse.SC_OK);
    }
}
