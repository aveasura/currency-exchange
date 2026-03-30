package org.exchanger.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.exchanger.config.ContextAttributes;
import org.exchanger.dto.request.ExchangeRateRequest;
import org.exchanger.dto.response.ExchangeRateResponse;
import org.exchanger.service.ExchangeRateService;
import org.exchanger.servlet.parser.ExchangeRateParser;
import org.exchanger.servlet.parser.RequestParser;
import org.exchanger.validator.ExchangeRateRequestValidator;
import org.exchanger.validator.RequestValidator;

import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends AbstractApiServlet {

    private ExchangeRateService exchangeRateService;
    private RequestParser<ExchangeRateRequest> parser;
    private RequestValidator<ExchangeRateRequest> validator;

    @Override
    public void init() {
        super.init();
        exchangeRateService = getService(ContextAttributes.EXCHANGE_RATE_SERVICE, ExchangeRateService.class);
        this.parser = new ExchangeRateParser();
        this.validator = new ExchangeRateRequestValidator();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        List<ExchangeRateResponse> exchangeRates = exchangeRateService.getAll();
        sendResponse(response, exchangeRates, HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        ExchangeRateRequest requestDto = parser.parse(request);
        validator.validate(requestDto);
        ExchangeRateResponse responseDto = exchangeRateService.addExchangeRate(requestDto);

        sendResponse(response, responseDto, HttpServletResponse.SC_CREATED);
    }
}
