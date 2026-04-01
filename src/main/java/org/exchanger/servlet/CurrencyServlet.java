package org.exchanger.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.exchanger.config.ContextAttributes;
import org.exchanger.dto.response.CurrencyResponse;
import org.exchanger.service.CurrencyService;
import org.exchanger.servlet.parser.CurrencyCodeParser;
import org.exchanger.servlet.parser.RequestParser;
import org.exchanger.validator.CurrencyCodeValidator;
import org.exchanger.validator.RequestValidator;

@WebServlet("/currency/*")
public class CurrencyServlet extends AbstractApiServlet {

    private CurrencyService currencyService;
    private RequestParser<String> parser;
    private RequestValidator<String> validator;

    @Override
    public void init() {
        super.init();
        currencyService = getService(ContextAttributes.CURRENCY_SERVICE, CurrencyService.class);
        this.parser = new CurrencyCodeParser();
        this.validator = new CurrencyCodeValidator();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String code = parser.parse(request);
        validator.validate(code);
        CurrencyResponse responseDto = currencyService.getByCurrencyCode(code);

        sendResponse(response, responseDto, HttpServletResponse.SC_OK);
    }
}
