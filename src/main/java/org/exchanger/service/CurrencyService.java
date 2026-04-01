package org.exchanger.service;

import org.exchanger.dto.request.CurrencyRequest;
import org.exchanger.dto.response.CurrencyResponse;

public interface CurrencyService
        extends CreateService<CurrencyRequest, CurrencyResponse>,
                GetAllService<CurrencyResponse> {

    CurrencyResponse getByCurrencyCode(String currencyCode);
}
