package org.exchanger.service;

import org.exchanger.dto.request.ExchangeRateRequest;
import org.exchanger.dto.request.UpdateExchangeRateRequest;
import org.exchanger.dto.response.ExchangeRateResponse;
import org.exchanger.dto.response.UpdateExchangeRateResponse;

public interface ExchangeRateService
        extends CreateService<ExchangeRateRequest, ExchangeRateResponse>,
                GetAllService<ExchangeRateResponse> {

    ExchangeRateResponse get(String baseCurrencyCode, String targetCurrencyCode);

    UpdateExchangeRateResponse updateExchangeRate(UpdateExchangeRateRequest request);
}
