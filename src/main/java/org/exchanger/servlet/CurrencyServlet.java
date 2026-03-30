package org.exchanger.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.exchanger.config.ContextAttributes;
import org.exchanger.dto.response.CurrencyResponse;
import org.exchanger.dto.response.ErrorResponse;
import org.exchanger.exception.AppException;
import org.exchanger.service.CurrencyService;
import org.exchanger.servlet.parser.CurrencyCodeParser;
import org.exchanger.servlet.parser.RequestParser;

@WebServlet("/currency/*")
public class CurrencyServlet extends AbstractApiServlet {

    private CurrencyService currencyService;
    private RequestParser<String> parser;

    @Override
    public void init() {
        super.init();
        currencyService = getService(ContextAttributes.CURRENCY_SERVICE, CurrencyService.class);
        this.parser = new CurrencyCodeParser();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            String code = parser.parse(request);
            // todo validate(code);

            CurrencyResponse responseDto = currencyService.get(code);
            sendResponse(response, responseDto, HttpServletResponse.SC_OK);
        } catch (AppException e) {
            sendResponse(response, new ErrorResponse(e.getMessage()), e.getStatus());
        }
    }
}
