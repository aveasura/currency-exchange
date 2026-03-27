package org.exchanger.service;

import org.exchanger.dto.response.CreateCurrencyResponse;
import org.exchanger.dto.response.ExchangeResponse;
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

    // todo reverse
    public ExchangeResponse convert(String from, String to, String amount) {
        Currency base = currencyRepository.findCurrency(from);
        Currency target = currencyRepository.findCurrency(to);

        ExchangeRate exchangeRate = exchangeRateRepository.find(base.getId(), target.getId());

        BigDecimal rate = exchangeRate.getRate();
        BigDecimal quantity = new BigDecimal(amount);

//        if (rate == null) {
//            ExchangeRate reverse = exchangeRateRepository.find(target.getId(), base.getId());
//            BigDecimal newRate = reverse.getRate();
//            BigDecimal converted = BigDecimal.ONE.divide(newRate, 2, RoundingMode.HALF_UP);
//            return new ExchangeResponse(base, target, newRate, quantity, converted);
//        }

        BigDecimal convertedAmount = quantity.multiply(rate);

        // todo mapper
        CreateCurrencyResponse baseCurrencyDto = new CreateCurrencyResponse(
                base.getId(),
                base.getFullName(),
                base.getCode(),
                base.getSign()
        );

        CreateCurrencyResponse targetCurrencyDto = new CreateCurrencyResponse(
                target.getId(),
                target.getFullName(),
                target.getCode(),
                target.getSign()
        );

        ExchangeResponse dto = new ExchangeResponse(baseCurrencyDto, targetCurrencyDto, rate, quantity, convertedAmount);

        return dto;
    }
}
