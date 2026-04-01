package org.exchanger.service.impl;

import org.exchanger.model.Currency;
import org.exchanger.repository.CurrencyRepository;

import java.util.Locale;

public abstract class AbstractCurrencyLookupService {
    protected final CurrencyRepository currencyRepository;

    public AbstractCurrencyLookupService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    protected Currency getCurrency(String currencyCode) {
        String normalizedCurrencyCode = normalize(currencyCode);
        return currencyRepository.findByCode(normalizedCurrencyCode);
    }

    private String normalize(String code) {
        return code.trim().toUpperCase(Locale.ROOT);
    }
}
