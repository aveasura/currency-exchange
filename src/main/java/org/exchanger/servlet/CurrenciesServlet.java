package org.exchanger.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.exchanger.dto.request.CreateCurrencyRequest;
import org.exchanger.dto.response.CreateCurrencyResponse;
import org.exchanger.service.CurrencyService;

import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends BaseServlet {

    private CurrencyService currencyService;

    @Override
    public void init() {
        super.init();
        currencyService = getService("currencyService", CurrencyService.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        List<CreateCurrencyResponse> currencies = currencyService.getAll();
        sendJsonResponse(response, currencies, HttpServletResponse.SC_OK);
    }


    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        String name = request.getParameter("name");
        String code = request.getParameter("code");
        String sign = request.getParameter("sign");

        CreateCurrencyRequest currency = new CreateCurrencyRequest(name, code, sign);
        currencyService.createCurrency(currency);

        sendJsonResponse(response, currency, HttpServletResponse.SC_CREATED);
    }
}
