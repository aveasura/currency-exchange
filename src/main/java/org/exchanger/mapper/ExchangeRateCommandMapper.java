package org.exchanger.mapper;

import org.exchanger.dto.request.ExchangeRateRequest;
import org.exchanger.dto.request.UpdateExchangeRateRequest;
import org.exchanger.service.command.CreateExchangeRateCommand;
import org.exchanger.service.command.UpdateExchangeRateCommand;

import java.math.BigDecimal;

public final class ExchangeRateCommandMapper {

    public CreateExchangeRateCommand toCreateCommand(ExchangeRateRequest request) {
        return new CreateExchangeRateCommand(
                request.baseCurrencyCode(),
                request.targetCurrencyCode(),
                new BigDecimal(request.rate())
        );
    }

    public UpdateExchangeRateCommand toUpdateCommand(UpdateExchangeRateRequest request) {
        return new UpdateExchangeRateCommand(
                request.baseCurrencyCode(),
                request.targetCurrencyCode(),
                new BigDecimal(request.rate())
        );
    }
}