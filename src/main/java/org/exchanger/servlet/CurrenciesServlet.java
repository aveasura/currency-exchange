package org.exchanger.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.exchanger.dto.request.CurrencyRequest;
import org.exchanger.dto.response.CurrencyResponse;
import org.exchanger.service.CurrencyService;

import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends AbstractApiServlet {

    private CurrencyService currencyService;

    @Override
    public void init() {
        super.init();
        currencyService = getService("currencyService", CurrencyService.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        List<CurrencyResponse> currencies = currencyService.getAll();
        sendJsonResponse(response, currencies, HttpServletResponse.SC_OK);
    }


    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        String name = request.getParameter("name");
        String code = request.getParameter("code");
        String sign = request.getParameter("sign");

        CurrencyRequest requestDto = new CurrencyRequest(name, code, sign);
        CurrencyResponse responseDto = currencyService.createCurrency(requestDto);

        sendJsonResponse(response, responseDto, HttpServletResponse.SC_CREATED);
    }
}
