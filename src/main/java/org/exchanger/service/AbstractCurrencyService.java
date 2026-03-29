package org.exchanger.service;

import org.exchanger.model.Currency;
import org.exchanger.repository.CurrencyRepository;

public abstract class AbstractCurrencyService {
    protected final CurrencyRepository currencyRepository;

    public AbstractCurrencyService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    protected Currency getCurrency(String code) {
        Currency currency = currencyRepository.findCurrency(code);
        return currency;
    }
}
