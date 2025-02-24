package org.myapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.myapp.dto.CurrencyDto;
import org.myapp.dto.ExchangeRateDto;
import org.myapp.service.CurrenciesService;
import org.myapp.service.ExchangeRateService;

import java.io.IOException;
import java.util.List;

@WebServlet("/exchangeRate/*")
public class ExchangeRateController extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private CurrenciesService currenciesService;
    private ExchangeRateService exchangeRateService;

    @Override
    public void init() throws ServletException {
        currenciesService = (CurrenciesService) getServletContext().getAttribute("currenciesService");
        exchangeRateService = (ExchangeRateService) getServletContext().getAttribute("exchangeRatesService");
    }

    // /exchangeRate/USDEUR
    // todo обработка ответов
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (isInvalidPath(pathInfo)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL parameter");
            return;
        }

        String actualPath = getActualPath(pathInfo);
        if (isInvalidLength(actualPath)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Path must be 6 characters long");
            return;
        }

        try {
            List<CurrencyDto> currencies = currenciesService.getCurrenciesByPath(actualPath);
            ExchangeRateDto rate = exchangeRateService.getExchangeRate(currencies);

            sendJsonResponse(resp, rate, HttpServletResponse.SC_OK);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }

    // todo patch. (Внесение изменений в rate валют)
    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    private void sendJsonResponse(HttpServletResponse resp, ExchangeRateDto rate, int status) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setStatus(status);

        objectMapper.writeValue(resp.getWriter(), rate);
    }

    private boolean isInvalidLength(String actualPath) {
        return actualPath.length() != 6;
    }

    private boolean isInvalidPath(String pathInfo) {
        return pathInfo == null || pathInfo.isBlank();
    }

    private String getActualPath(String pathInfo) {
        return pathInfo.substring(1).toUpperCase();
    }
}
