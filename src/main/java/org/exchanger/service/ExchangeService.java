package org.exchanger.service;

import org.exchanger.dto.request.ExchangeRequest;
import org.exchanger.dto.response.CurrencyResponse;
import org.exchanger.dto.response.ExchangeResponse;
import org.exchanger.model.Currency;
import org.exchanger.model.ExchangeRate;
import org.exchanger.repository.CurrencyRepository;
import org.exchanger.repository.ExchangeRateRepository;

import java.math.BigDecimal;

public class ExchangeService extends AbstractCurrencyService {

    private final ExchangeRateRepository exchangeRateRepository;

    public ExchangeService(CurrencyRepository currencyRepository, ExchangeRateRepository exchangeRateRepository) {
        super(currencyRepository);
        this.exchangeRateRepository = exchangeRateRepository;
    }

    // todo reverse convert
    public ExchangeResponse convert(ExchangeRequest dto) {
        Currency base = getCurrency(dto.from());
        Currency target = getCurrency(dto.to());
        BigDecimal quantity = new BigDecimal(dto.amount());

        ExchangeRate exchangeRate = exchangeRateRepository.find(base.getId(), target.getId());
        BigDecimal rate = exchangeRate.getRate();
        BigDecimal convertedAmount = quantity.multiply(rate);

        // todo mapper
        CurrencyResponse baseCurrencyDto = new CurrencyResponse(
                base.getId(),
                base.getFullName(),
                base.getCode(),
                base.getSign()
        );

        CurrencyResponse targetCurrencyDto = new CurrencyResponse(
                target.getId(),
                target.getFullName(),
                target.getCode(),
                target.getSign()
        );

        ExchangeResponse responseDto = new ExchangeResponse(
                baseCurrencyDto,
                targetCurrencyDto,
                rate,
                quantity,
                convertedAmount);

        return responseDto;
    }
}
