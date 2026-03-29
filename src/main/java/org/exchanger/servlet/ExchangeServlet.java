package org.exchanger.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.exchanger.dto.request.ExchangeRequest;
import org.exchanger.dto.response.ExchangeResponse;
import org.exchanger.service.ExchangeService;

@WebServlet("/exchange")
public class ExchangeServlet extends AbstractApiServlet {

    private ExchangeService exchangeService;

    @Override
    public void init() {
        super.init();
        exchangeService = getService("exchangeService", ExchangeService.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String from = request.getParameter("from");
        String to = request.getParameter("to");
        String amount = request.getParameter("amount");

        ExchangeRequest requestDto = new ExchangeRequest(from, to, amount);
        ExchangeResponse responseDto = exchangeService.convert(requestDto);

        sendJsonResponse(response, responseDto, HttpServletResponse.SC_OK);
    }
}
