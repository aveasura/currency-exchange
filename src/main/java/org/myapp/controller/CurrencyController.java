package org.myapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.myapp.error.OperationResult;
import org.myapp.service.CurrenciesService;

import java.io.IOException;

@WebServlet("/currency/*")
public class CurrencyController extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private CurrenciesService currenciesService;

    @Override
    public void init() throws ServletException {
        currenciesService = (CurrenciesService) getServletContext().getAttribute("currenciesService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (!isPathValid(pathInfo)) {
            sendJsonResponse(resp, new OperationResult(false, "Currency code is missing").getMessage(),
                    HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String code = pathInfo.replaceFirst("/", "").toUpperCase();
        OperationResult result = currenciesService.getCurrency(code);

        if (!result.isSuccess()) {
            sendJsonResponse(resp, result.getMessage(), HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        sendJsonResponse(resp, result, HttpServletResponse.SC_OK);
    }

    // TODO реализовать post
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {}

    private boolean isPathValid(String pathInfo) {
        return pathInfo == null || pathInfo.isEmpty() || pathInfo.equals("/");
    }

    private void sendJsonResponse(HttpServletResponse resp, Object responseObject, int status) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(status);

        objectMapper.writeValue(resp.getWriter(), responseObject);
    }
}