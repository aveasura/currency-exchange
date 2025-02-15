package org.myapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.myapp.model.Currency;
import org.myapp.service.CurrenciesService;

import java.io.IOException;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesController extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private CurrenciesService service;

    @Override
    public void init() throws ServletException {
        service = (CurrenciesService) getServletContext().getAttribute("service");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Currency> currencies = service.getCurrencies();

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(HttpServletResponse.SC_OK);

        objectMapper.writeValue(resp.getWriter(), currencies);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Currency currency = objectMapper.readValue(req.getReader(), Currency.class);




        int generatedId = service.addCurrency(currency);
        if (generatedId > 0) {
            currency.setId(generatedId);
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(HttpServletResponse.SC_CREATED);
        objectMapper.writeValue(resp.getWriter(), currency);
    }
}