package org.exchanger.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.exchanger.model.Currency;
import org.exchanger.service.CurrencyService;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

// todo Дубликат по сути. Можно вынести в абстрактный servlet

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    private CurrencyService currencyService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        Object service = getServletContext().getAttribute("currencyService");
        Object mapper = getServletContext().getAttribute("objectMapper");

        if (!(service instanceof CurrencyService currencyService)) {
            throw new ServletException("CurrencyService not initialized");
        }

        if (!(mapper instanceof ObjectMapper objectMapper)) {
            throw new ServletException("ObjectMapper not initialized");
        }

        this.currencyService = currencyService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        List<Currency> currencies = currencyService.getAll();

        response.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(response.getWriter(), currencies);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String name = request.getParameter("name");
        String code = request.getParameter("code");
        String sign = request.getParameter("sign");

        // todo dto
        Currency currency = new Currency(name, code, sign);

        currencyService.createCurrency(currency);
        response.setStatus(HttpServletResponse.SC_CREATED);
        objectMapper.writeValue(response.getWriter(), currency);
    }
}
