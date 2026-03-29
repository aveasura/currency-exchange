package org.exchanger.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.exchanger.dto.request.UpdateExchangeRateRequest;
import org.exchanger.dto.response.ExchangeRateResponse;
import org.exchanger.dto.response.UpdateExchangeRateResponse;
import org.exchanger.service.ExchangeRateService;

import java.io.IOException;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends AbstractApiServlet {

    private ExchangeRateService exchangeRateService;

    @Override
    public void init() {
        super.init();
        exchangeRateService = getService("exchangeRateService", ExchangeRateService.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        // todo path parser
        String pathInfo = request.getPathInfo();
        String pair = pathInfo.substring(1);

        ExchangeRateResponse responseDto = exchangeRateService.get(pair);
        sendJsonResponse(response, responseDto, HttpServletResponse.SC_OK);
    }


    // todo ref
    @Override
    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String pathInfo = request.getPathInfo();
        String pair = pathInfo.substring(1);

        String base = pair.substring(0, 3);
        String target = pair.substring(3, 6);

        String rate = extractRate(request);

        UpdateExchangeRateRequest updateRequest = new UpdateExchangeRateRequest(base, target, rate);

        UpdateExchangeRateResponse responseDto = exchangeRateService.patchExchangeRate(updateRequest);
        sendJsonResponse(response, responseDto, HttpServletResponse.SC_OK);
    }

    private String extractRate(HttpServletRequest request) throws IOException {
        String body = request.getReader().readLine();
        if (body == null || !body.startsWith("rate=")) {
            return null;
        }
        return java.net.URLDecoder.decode(
                body.substring("rate=".length()),
                java.nio.charset.StandardCharsets.UTF_8
        );
    }
}
