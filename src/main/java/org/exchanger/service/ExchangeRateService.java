package org.exchanger.service;

import org.exchanger.model.Currency;
import org.exchanger.model.ExchangeRate;
import org.exchanger.repository.CurrencyRepository;
import org.exchanger.repository.ExchangeRateRepository;

import java.util.List;

public class ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final CurrencyRepository currencyRepository;

    public ExchangeRateService(ExchangeRateRepository exchangeRateRepository, CurrencyRepository currencyRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
        this.currencyRepository = currencyRepository;
    }

    public List<ExchangeRate> getAll() {
        List<ExchangeRate> exchangeRates = exchangeRateRepository.findAll();

        return exchangeRates;
    }

    // todo парсим по 3 символа в две карренси
    public ExchangeRate get(String pair) {
        String baseCurrencyCode = pair.substring(0, 3);
        String targetCurrencyCode = pair.substring(3, 6);

        Currency base = currencyRepository.findCurrency(baseCurrencyCode);
        Currency target = currencyRepository.findCurrency(targetCurrencyCode);

        ExchangeRate exchangeRate = exchangeRateRepository.find(base.getId(), target.getId());

        return exchangeRate;
    }
}
