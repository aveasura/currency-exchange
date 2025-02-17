package org.myapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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

        if (currencyId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Код валюты отсутствует в адресе");
            return;
        }

        CurrencyDto currency = service.getCurrency(currencyId);
        if (currency == null) {
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"message\": \"Валюта не найдена\"}");
            return;
        }

        String acceptHeader = req.getHeader("Accept");
        if (acceptHeader != null && acceptHeader.contains("application/json")) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            String json = String.format(
                    "{\"id\": %d, \"name\": \"%s\", \"code\": \"%s\", \"sign\": \"%s\"}",
                    currency.getId(), currency.getFullName(), currency.getCode(), currency.getSign()
            );

            resp.getWriter().write(json);
        } else {
            req.setAttribute("currency", currency);
            req.getRequestDispatcher("/WEB-INF/views/currency.jsp").forward(req, resp);
        }
    }
}