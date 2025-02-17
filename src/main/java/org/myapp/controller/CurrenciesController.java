package org.myapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.myapp.dto.CurrencyDto;
import org.myapp.mapper.CurrencyMapper;
import org.myapp.model.Currency;
import org.myapp.service.CurrenciesService;

import java.io.IOException;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesController extends HttpServlet {
    private static final String JSON_CONTENT_TYPE = "application/json";
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

        if (service.isJson(acceptHeader)) {
            sendJsonResponse(resp, currencies);
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

        if (JSON_CONTENT_TYPE.equalsIgnoreCase(req.getContentType())) {
            sendJsonResponse(resp, CurrencyMapper.toDto(currency));
        } else {
            resp.sendRedirect(req.getContextPath() + "/");
        }
    }

    private void sendJsonResponse(HttpServletResponse resp, Object responseObject) throws IOException {
        resp.setContentType(JSON_CONTENT_TYPE);
        resp.setCharacterEncoding("UTF-8");

        if (service.isList(responseObject)) {
            resp.setStatus(HttpServletResponse.SC_OK); // для списка
        } else {
            resp.setStatus(HttpServletResponse.SC_CREATED); // для одного объектиа
        }

        objectMapper.writeValue(resp.getWriter(), responseObject);
    }

    private CurrencyDto extractCurrencyDto(HttpServletRequest req) throws IOException {
        if (JSON_CONTENT_TYPE.equalsIgnoreCase(req.getContentType())) {
            return objectMapper.readValue(req.getReader(), CurrencyDto.class);
        }
        String code = req.getParameter("code");
        String name = req.getParameter("fullName");
        String sign = req.getParameter("sign");
        return service.createCurrencyDto(code, name, sign);
    }
}