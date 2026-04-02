package org.exchanger.service;

import org.exchanger.dto.response.ExchangeRateResponse;
import org.exchanger.dto.response.UpdateExchangeRateResponse;
import org.exchanger.service.command.CreateExchangeRateCommand;
import org.exchanger.service.command.UpdateExchangeRateCommand;

public interface ExchangeRateService
        extends CreateService<CreateExchangeRateCommand, ExchangeRateResponse>,
        GetAllService<ExchangeRateResponse> {

    ExchangeRateResponse get(String baseCurrencyCode, String targetCurrencyCode);

    UpdateExchangeRateResponse updateExchangeRate(UpdateExchangeRateCommand command);
}
