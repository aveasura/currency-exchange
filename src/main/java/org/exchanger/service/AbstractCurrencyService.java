package org.exchanger.service;

import org.exchanger.model.Currency;
import org.exchanger.repository.CurrencyRepository;

import java.util.Locale;

public abstract class AbstractCurrencyService {
    protected final CurrencyRepository currencyRepository;

    public AbstractCurrencyService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    protected Currency getCurrency(String code) {
        String normalizedCode = normalize(code);
        return currencyRepository.findByCode(normalizedCode);
    }

    private String normalize(String code) {
        return code.trim().toUpperCase(Locale.ROOT);
    }
}
