package org.exchanger.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.exchanger.ExchangeResponseDto;
import org.exchanger.service.ExchangeService;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {

    private ExchangeService exchangeService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        Object service = getServletContext().getAttribute("exchangeService");
        Object mapper = getServletContext().getAttribute("objectMapper");

        if (!(service instanceof ExchangeService exchangeService)) {
            throw new ServletException("exchangeService not found");
        }

        if (!(mapper instanceof ObjectMapper objectMapper)) {
            throw new ServletException("objectMapper not sound");
        }

        this.exchangeService = exchangeService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // todo dto request
        String from = request.getParameter("from");
        String to = request.getParameter("to");
        String amount = request.getParameter("amount");

        ExchangeResponseDto dto = exchangeService.convert(from, to, amount);

        response.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(response.getWriter(), dto);
    }
}
