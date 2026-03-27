package org.exchanger.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.exchanger.dto.response.CurrencyResponse;
import org.exchanger.service.CurrencyService;

@WebServlet("/currency/*")
public class CurrencyServlet extends AbstractApiServlet {

    private CurrencyService currencyService;

    @Override
    public void init() {
        super.init();
        currencyService = getService("currencyService", CurrencyService.class);
    }


    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        // todo parser class
        String pathInfo = request.getPathInfo();
        String code = pathInfo.substring(1);

        CurrencyResponse responseDto = currencyService.get(code);
        sendJsonResponse(response, responseDto, HttpServletResponse.SC_OK);
    }
}
