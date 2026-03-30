package org.exchanger.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.exchanger.config.ContextAttributes;
import org.exchanger.dto.request.UpdateExchangeRateRequest;
import org.exchanger.dto.response.ExchangeRateResponse;
import org.exchanger.dto.response.UpdateExchangeRateResponse;
import org.exchanger.service.ExchangeRateService;
import org.exchanger.servlet.parser.CurrencyPairParser;
import org.exchanger.servlet.parser.CurrencyPairRequest;
import org.exchanger.servlet.parser.RequestParser;
import org.exchanger.servlet.parser.UpdateRateParser;
import org.exchanger.validator.RequestValidator;
import org.exchanger.validator.UpdateExchangeRateValidator;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends AbstractApiServlet {

    private ExchangeRateService exchangeRateService;
    private RequestParser<CurrencyPairRequest> codeParser;
    private RequestParser<UpdateExchangeRateRequest> updateParser;
    private RequestValidator<UpdateExchangeRateRequest> validator;

    @Override
    public void init() {
        super.init();
        exchangeRateService = getService(ContextAttributes.EXCHANGE_RATE_SERVICE, ExchangeRateService.class);
        this.codeParser = new CurrencyPairParser();
        this.updateParser = new UpdateRateParser(codeParser);
        this.validator = new UpdateExchangeRateValidator();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        CurrencyPairRequest pair = codeParser.parse(request);
        ExchangeRateResponse responseDto = exchangeRateService.get(pair.base(), pair.target());

        sendResponse(response, responseDto, HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPatch(HttpServletRequest request, HttpServletResponse response) {
        UpdateExchangeRateRequest updateRequest = updateParser.parse(request);
        validator.validate(updateRequest);
        UpdateExchangeRateResponse responseDto = exchangeRateService.patchExchangeRate(updateRequest);

        sendResponse(response, responseDto, HttpServletResponse.SC_OK);
    }
}
