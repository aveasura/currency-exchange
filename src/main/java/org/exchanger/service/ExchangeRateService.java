package org.exchanger.service;

import org.exchanger.model.ExchangeRate;
import org.exchanger.repository.ExchangeRateRepository;

import java.util.List;

public class ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;

    public ExchangeRateService(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }

    public List<ExchangeRate> getAll() {
        List<ExchangeRate> exchangeRates = exchangeRateRepository.findAll();

        return exchangeRates;
    }
}
