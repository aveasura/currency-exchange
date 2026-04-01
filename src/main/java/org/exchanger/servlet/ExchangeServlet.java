package org.exchanger.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.exchanger.dto.request.ExchangeRequest;
import org.exchanger.dto.response.ExchangeResponse;
import org.exchanger.service.ExchangeService;
import org.exchanger.servlet.parser.ExchangeRequestParser;
import org.exchanger.servlet.parser.RequestParser;
import org.exchanger.validator.ExchangeRequestValidator;
import org.exchanger.validator.RequestValidator;

@WebServlet({"/exchange", "/exchange/"})
public class ExchangeServlet extends AbstractApiServlet {

    private ExchangeService exchangeService;
    private RequestParser<ExchangeRequest> parser;
    private RequestValidator<ExchangeRequest> validator;

    @Override
    public void init() {
        super.init();
        exchangeService = components.exchangeService();
        this.parser = new ExchangeRequestParser();
        this.validator = new ExchangeRequestValidator();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        ExchangeRequest requestDto = parser.parse(request);
        validator.validate(requestDto);
        ExchangeResponse responseDto = exchangeService.convert(requestDto);

        sendResponse(response, responseDto, HttpServletResponse.SC_OK);
    }
}
