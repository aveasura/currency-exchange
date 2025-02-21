package org.myapp.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.myapp.model.ExchangeRate;
import org.myapp.service.CurrenciesService;
import org.myapp.service.ExchangeRateService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRateController extends HttpServlet {

    private ExchangeRateService exchangeRateService;
    private CurrenciesService currencyService;

    @Override
    public void init() throws ServletException {
        exchangeRateService = (ExchangeRateService) getServletContext().getAttribute("exchangeRatesService");
        currencyService = (CurrenciesService) getServletContext().getAttribute("currencyService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<ExchangeRate> exchangeRateList = exchangeRateService.getExchangeRates();

        req.setAttribute("ratesList", exchangeRateList);
        req.getRequestDispatcher("/WEB-INF/views/exchangeList.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String baseCurrId = req.getParameter("from");
        String targetCurrId = req.getParameter("to");
        String rate = req.getParameter("rate");

//        CurrencyDto from = currencyService.getCurrency(baseCurrId);
//        CurrencyDto to = currencyService.getCurrency(targetCurrId);

        try {
            BigDecimal exchangeRate = new BigDecimal(rate);
//            service.addExchangeRate(from, to, exchangeRate);
            resp.sendRedirect(req.getContextPath() + "/exchangeRates");
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Ошибка: курс валют должен быть числом.");
            req.getRequestDispatcher("/exchangeRatesForm.jsp").forward(req, resp);
        }
    }
}
