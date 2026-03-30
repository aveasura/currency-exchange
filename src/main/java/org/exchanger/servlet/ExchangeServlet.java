package org.exchanger.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.exchanger.dto.response.ErrorResponse;
import org.exchanger.dto.request.ExchangeRequest;
import org.exchanger.dto.response.ExchangeResponse;
import org.exchanger.exception.AppException;
import org.exchanger.exception.BadRequestException;
import org.exchanger.service.ExchangeService;

@WebServlet("/exchange")
public class ExchangeServlet extends AbstractApiServlet {

    private ExchangeService exchangeService;

    @Override
    public void init() {
        super.init();
        exchangeService = getService("exchangeService", ExchangeService.class);
    }

    // todo parser ExchangeRequest
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            String from = request.getParameter("from");
            String to = request.getParameter("to");
            String amount = request.getParameter("amount");
            // todo validation
            if (from == null || from.isEmpty() || to == null || to.isEmpty() || amount == null || amount.isEmpty()) {
                throw new BadRequestException("Field 'from', 'to', 'amount' are required");
            }

            ExchangeRequest requestDto = new ExchangeRequest(from, to, amount);
            ExchangeResponse responseDto = exchangeService.convert(requestDto);

            sendResponse(response, responseDto, HttpServletResponse.SC_OK);
        } catch (AppException e) {
            sendResponse(response, new ErrorResponse(e.getMessage()), e.getStatus());
        }
    }
}
