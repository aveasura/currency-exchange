package org.myapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.myapp.dto.ExchangeRateDto;
import org.myapp.service.ExchangeRateService;

import java.io.IOException;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesController extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private ExchangeRateService exchangeRateService;

    @Override
    public void init() throws ServletException {
        exchangeRateService = (ExchangeRateService) getServletContext().getAttribute("exchangeRatesService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<ExchangeRateDto> exchangeRateList = exchangeRateService.getExchangeRates();
        sendJsonResponse(resp, exchangeRateList, HttpServletResponse.SC_OK);
    }

    private void sendJsonResponse(HttpServletResponse resp, Object responseObject, int status) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(status);

        objectMapper.writeValue(resp.getWriter(), responseObject);
    }
}
