package org.exchanger.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.exchanger.dto.response.CurrencyResponse;
import org.exchanger.service.CurrencyService;
import org.exchanger.servlet.parser.CodeParser;

@WebServlet("/currency/*")
public class CurrencyServlet extends AbstractApiServlet {

    private CurrencyService currencyService;
    private CodeParser parser;

    @Override
    public void init() {
        super.init();
        currencyService = getService("currencyService", CurrencyService.class);
        parser = getService("codeParser", CodeParser.class);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        String code = parser.getCleanPath(request);
        // todo validate(code);

        CurrencyResponse responseDto = currencyService.get(code);
        sendJsonResponse(response, responseDto, HttpServletResponse.SC_OK);
    }
}
