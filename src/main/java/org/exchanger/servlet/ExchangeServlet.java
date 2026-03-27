package org.exchanger.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

        ExchangeResponse exchangeDto = exchangeService.convert(from, to, amount);
        sendJsonResponse(response, exchangeDto, HttpServletResponse.SC_OK);
    }
}
