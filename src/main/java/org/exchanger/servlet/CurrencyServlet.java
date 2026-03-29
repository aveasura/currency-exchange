package org.exchanger.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.exchanger.dto.ErrorResponse;
import org.exchanger.dto.response.CurrencyResponse;
import org.exchanger.exception.AppException;
import org.exchanger.exception.BadRequestException;
import org.exchanger.service.CurrencyService;
import org.exchanger.servlet.parser.CodeParser;

@WebServlet("/currency/*")
public class CurrencyServlet extends AbstractApiServlet {

    private CurrencyService currencyService;
    private CodeParser parser;

    @Override
    public void init() {
        super.init();
        currencyService = getService("currencyService", CurrencyService.class);
        parser = getService("codeParser", CodeParser.class);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            String code = parser.getCleanPath(request);
            // todo validate(code);
            if (code == null || code.isEmpty()) {
                throw new BadRequestException("Field 'code' is required");
            }

            CurrencyResponse responseDto = currencyService.get(code);
            sendResponse(response, responseDto, HttpServletResponse.SC_OK);
        } catch (AppException e) {
            sendResponse(response, new ErrorResponse(e.getMessage()), e.getStatus());
        }
    }
}
