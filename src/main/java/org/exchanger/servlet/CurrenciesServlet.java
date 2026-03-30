package org.exchanger.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.exchanger.config.ContextAttributes;
import org.exchanger.dto.request.CurrencyRequest;
import org.exchanger.dto.response.CurrencyResponse;
import org.exchanger.service.CurrencyService;
import org.exchanger.servlet.parser.CurrencyRequestParser;
import org.exchanger.servlet.parser.RequestParser;
import org.exchanger.validator.CurrencyRequestValidator;
import org.exchanger.validator.RequestValidator;

import java.util.List;

@WebServlet({"/currencies", "/currencies/"})
public class CurrenciesServlet extends AbstractApiServlet {

    private CurrencyService currencyService;
    private RequestParser<CurrencyRequest> parser;
    private RequestValidator<CurrencyRequest> validator;

    @Override
    public void init() {
        super.init();
        currencyService = getService(ContextAttributes.CURRENCY_SERVICE, CurrencyService.class);
        this.parser = new CurrencyRequestParser();
        this.validator = new CurrencyRequestValidator();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        List<CurrencyResponse> currencies = currencyService.getAll();
        sendResponse(response, currencies, HttpServletResponse.SC_OK);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        CurrencyRequest requestDto = parser.parse(request);
        validator.validate(requestDto);
        CurrencyResponse responseDto = currencyService.createCurrency(requestDto);

        sendResponse(response, responseDto, HttpServletResponse.SC_CREATED);
    }
}
