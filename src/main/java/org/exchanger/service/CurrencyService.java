package org.exchanger.service;

import org.exchanger.model.Currency;
import org.exchanger.repository.CurrencyRepository;

import java.util.List;

// todo
public class CurrencyService {

    private final CurrencyRepository currencyRepository;

    public CurrencyService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    public Currency getCurrency(String code) {
        Currency currency = currencyRepository.findCurrency(code);

        return currency;
    }

    public List<Currency> getAll() {
        List<Currency> currencies = currencyRepository.findAll();

        return currencies;
    }

    public void createCurrency(Currency currency) {
        currencyRepository.create(currency);
    }
}
