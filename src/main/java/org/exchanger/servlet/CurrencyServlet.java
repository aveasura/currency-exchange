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

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {

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
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();
        String code = pathInfo.substring(1);

        Currency currency = currencyService.getCurrency(code);

        response.setStatus(200);
        objectMapper.writeValue(response.getWriter(), currency);
    }
}
