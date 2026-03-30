package org.exchanger.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.exchanger.dto.request.ExchangeRequest;
import org.exchanger.dto.response.ErrorResponse;
import org.exchanger.dto.response.ExchangeResponse;
import org.exchanger.exception.AppException;
import org.exchanger.service.ExchangeService;
import org.exchanger.servlet.parser.ExchangeRequestParser;
import org.exchanger.servlet.parser.RequestParser;

@WebServlet("/exchange")
public class ExchangeServlet extends AbstractApiServlet {

    private ExchangeService exchangeService;
    private RequestParser<ExchangeRequest> parser;

    @Override
    public void init() {
        super.init();
        exchangeService = getService("exchangeService", ExchangeService.class);
        this.parser = new ExchangeRequestParser();
    }

    // todo parser ExchangeRequest
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            ExchangeRequest requestDto = parser.parse(request);
            // todo validate(requestDto)

            ExchangeResponse responseDto = exchangeService.convert(requestDto);

            sendResponse(response, responseDto, HttpServletResponse.SC_OK);
        } catch (AppException e) {
            sendResponse(response, new ErrorResponse(e.getMessage()), e.getStatus());
        }
    }
}
