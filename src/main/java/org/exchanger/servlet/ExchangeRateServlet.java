package org.exchanger.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.exchanger.model.ExchangeRate;
import org.exchanger.service.ExchangeRateService;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

// todo
@WebServlet("/exchangeRates")
public class ExchangeRateServlet extends HttpServlet {

    private ExchangeRateService exchangeRateService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        Object exchangeRate = getServletContext().getAttribute("exchangeRateService");
        Object mapper = getServletContext().getAttribute("objectMapper");
        if (!(exchangeRate instanceof ExchangeRateService exchangeRateService)) {
            throw new RuntimeException("aaaaaaaaaa");
        }

        if (!(mapper instanceof ObjectMapper objectMapper)) {
            throw new RuntimeException("bbbbbbbbb");
        }

        this.exchangeRateService = exchangeRateService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        List<ExchangeRate> exchangeRates = exchangeRateService.getAll();
        objectMapper.writeValue(response.getWriter(), exchangeRates);
    }
}
