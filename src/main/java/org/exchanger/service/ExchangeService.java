package org.exchanger.service;

import org.exchanger.dto.request.ExchangeRequest;
import org.exchanger.dto.response.CurrencyResponse;
import org.exchanger.dto.response.ExchangeResponse;
import org.exchanger.exception.ExchangeRateNotFoundException;
import org.exchanger.mapper.ResponseMapper;
import org.exchanger.model.Currency;
import org.exchanger.model.ExchangeRate;
import org.exchanger.repository.CurrencyRepository;
import org.exchanger.repository.ExchangeRateRepository;

import java.math.BigDecimal;

public class ExchangeService extends AbstractCurrencyService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final ResponseMapper<Currency, CurrencyResponse> currencyResponseMapper;

    public ExchangeService(CurrencyRepository currencyRepository,
                           ExchangeRateRepository exchangeRateRepository,
                           ResponseMapper<Currency, CurrencyResponse> currencyResponseMapper) {
        super(currencyRepository);
        this.exchangeRateRepository = exchangeRateRepository;
        this.currencyResponseMapper = currencyResponseMapper;
    }

    // todo reverse / cross rate
    public ExchangeResponse convert(ExchangeRequest request) {
        Currency base = getCurrency(request.from());
        Currency target = getCurrency(request.to());
        BigDecimal amount = new BigDecimal(request.amount());

        ExchangeRate exchangeRate = exchangeRateRepository.find(base.getId(), target.getId())
                .orElseThrow(() -> new ExchangeRateNotFoundException(base.getCode(), target.getCode()));

        BigDecimal rate = exchangeRate.getRate();
        BigDecimal convertedAmount = amount.multiply(rate);

        CurrencyResponse baseCurrencyDto = currencyResponseMapper.toDto(base);
        CurrencyResponse targetCurrencyDto = currencyResponseMapper.toDto(target);

        return new ExchangeResponse(
                baseCurrencyDto,
                targetCurrencyDto,
                rate,
                amount,
                convertedAmount);
    }
}
