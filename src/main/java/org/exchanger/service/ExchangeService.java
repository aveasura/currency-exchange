package org.exchanger.service;

import org.exchanger.ExchangeResponseDto;
import org.exchanger.model.Currency;
import org.exchanger.model.ExchangeRate;
import org.exchanger.repository.CurrencyRepository;
import org.exchanger.repository.ExchangeRateRepository;

import java.math.BigDecimal;

public class ExchangeService {

    private final CurrencyRepository currencyRepository;
    private final ExchangeRateRepository exchangeRateRepository;

    public ExchangeService(CurrencyRepository currencyRepository, ExchangeRateRepository exchangeRateRepository) {
        this.currencyRepository = currencyRepository;
        this.exchangeRateRepository = exchangeRateRepository;
    }


    public ExchangeResponseDto convert(String from, String to, String amount) {
        Currency base = currencyRepository.findCurrency(from);
        Currency target = currencyRepository.findCurrency(to);

        ExchangeRate exchangeRate = exchangeRateRepository.find(base.getId(), target.getId());

        BigDecimal rate = exchangeRate.getRate();
        BigDecimal quantity = new BigDecimal(amount);
        BigDecimal convertedAmount = quantity.multiply(rate);

        ExchangeResponseDto dto = new ExchangeResponseDto(base, target, rate, quantity, convertedAmount);
        return dto;
    }
}
