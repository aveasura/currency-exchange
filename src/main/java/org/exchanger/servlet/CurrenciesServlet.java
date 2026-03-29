package org.exchanger.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.exchanger.dto.ErrorResponse;
import org.exchanger.dto.request.CurrencyRequest;
import org.exchanger.dto.response.CurrencyResponse;
import org.exchanger.exception.AppException;
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
        try {
            List<CurrencyResponse> currencies = currencyService.getAll();
            sendResponse(response, currencies, HttpServletResponse.SC_OK);
        } catch (AppException e) {
            sendResponse(response, new ErrorResponse(e.getMessage()), e.getStatus());
        }
    }

    // todo parser CurrencyRequest
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        String name = request.getParameter("name");
        String code = request.getParameter("code");
        String sign = request.getParameter("sign");

        CurrencyRequest requestDto = new CurrencyRequest(name, code, sign);
        CurrencyResponse responseDto = currencyService.createCurrency(requestDto);

        sendResponse(response, responseDto, HttpServletResponse.SC_CREATED);
    }
}
