package org.myapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.myapp.dto.CurrencyDto;
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
        List<CurrencyDto> currencies = service.getCurrencies();
        String acceptHeader = req.getHeader("Accept");

        if (acceptHeader != null && acceptHeader.contains("application/json")) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getWriter(), currencies);
        } else {
            req.setAttribute("currList", currencies);
            req.getRequestDispatcher("/WEB-INF/views/currencyList.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CurrencyDto dto = extractCurrencyDto(req);

        Currency currency = service.addCurrency(dto);
        if (currency == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Invalid input\"}");
            return;
        }

        if ("application/json".equalsIgnoreCase(req.getContentType())) {
            sendJsonResponse(resp, currency);
        } else {
            resp.sendRedirect(req.getContextPath() + "/");
        }
    }

    private void sendJsonResponse(HttpServletResponse resp, Currency currency) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(HttpServletResponse.SC_CREATED);
        objectMapper.writeValue(resp.getWriter(), currency);
    }

    private CurrencyDto extractCurrencyDto(HttpServletRequest req) throws IOException {
        if ("application/json".equalsIgnoreCase(req.getContentType())) {
            return objectMapper.readValue(req.getReader(), CurrencyDto.class);
        }
        CurrencyDto dto = new CurrencyDto();
        dto.setCode(req.getParameter("code"));
        dto.setFullName(req.getParameter("fullName"));
        dto.setSign(req.getParameter("sign"));
        return dto;
    }
}