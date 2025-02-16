package org.myapp.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.myapp.dto.CurrencyDto;
import org.myapp.service.CurrenciesService;

import java.io.IOException;

@WebServlet("/currency/*")
public class CurrencyController extends HttpServlet {

    private CurrenciesService service;

    @Override
    public void init() throws ServletException {
        service = (CurrenciesService) getServletContext().getAttribute("service");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo(); // получить часть после /currency/
        String currencyId = null;

        if (pathInfo != null && pathInfo.length() > 1) {
            currencyId = pathInfo.substring(1); // Убираем первый слэш
        } else {
            currencyId = req.getParameter("id"); // Берем id из queryпараметра
        }

        if (currencyId != null) {
            CurrencyDto currency = service.getCurrency(currencyId);
            req.setAttribute("currency", currency);
        }

        req.getRequestDispatcher("/WEB-INF/views/currency.jsp").forward(req, resp);
    }
}