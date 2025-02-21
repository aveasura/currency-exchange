package org.myapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.myapp.dto.CurrencyDto;
import org.myapp.error.OperationResult;
import org.myapp.service.CurrenciesService;

import java.io.IOException;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesController extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private CurrenciesService currenciesService;

    @Override
    public void init() throws ServletException {
        currenciesService = (CurrenciesService) getServletContext().getAttribute("currenciesService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<CurrencyDto> currencies = currenciesService.getCurrencies();
        sendJsonResponse(resp, currencies, HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        CurrencyDto dto = extractCurrencyDto(req); // json
        CurrencyDto dto = currenciesService.createDto(
                req.getParameter("code"),
                req.getParameter("name"),
                req.getParameter(
                "sign"));

        OperationResult result = currenciesService.addCurrency(dto);

        if (!result.isSuccess()) {
            sendJsonResponse(resp, result.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        sendJsonResponse(resp, result.getDto(), HttpServletResponse.SC_CREATED);
    }

    private CurrencyDto extractCurrencyDto(HttpServletRequest req) throws IOException {
        return objectMapper.readValue(req.getReader(), CurrencyDto.class);
    }

    private void sendJsonResponse(HttpServletResponse resp, Object responseObject, int status) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(status);

        objectMapper.writeValue(resp.getWriter(), responseObject);
    }
}