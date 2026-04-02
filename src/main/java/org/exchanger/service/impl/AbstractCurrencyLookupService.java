package org.exchanger.service.impl;

import org.exchanger.model.Currency;
import org.exchanger.repository.CurrencyRepository;

public abstract class AbstractCurrencyLookupService {
    protected final CurrencyRepository currencyRepository;

    protected AbstractCurrencyLookupService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    protected Currency getCurrency(String currencyCode) {
        return currencyRepository.findByCode(currencyCode);
    }
}
