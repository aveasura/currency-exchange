package org.myapp.service;

import org.myapp.dao.CurrencyDao;
import org.myapp.dao.ExchangeRateDao;
import org.myapp.dto.CurrencyDto;
import org.myapp.dto.ExchangeRateDto;
import org.myapp.mapper.ExchangeRateMapper;
import org.myapp.model.Currency;
import org.myapp.model.ExchangeRate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateService {

    private final ExchangeRateDao exchangeRateDao;
    private final CurrencyDao currencyDao;

    public ExchangeRateService(ExchangeRateDao exchangeRateDao, CurrencyDao currencyDao) {
        this.exchangeRateDao = exchangeRateDao;
        this.currencyDao = currencyDao;
    }

    public int addExchangeRate(CurrencyDto fromDto, CurrencyDto toDto, BigDecimal exchangeRate) {
        Currency from = currencyDao.findByCode(fromDto.getCode());
        Currency to = currencyDao.findByCode(toDto.getCode());

        if (from == null || to == null) {
            throw new IllegalArgumentException("Одна из валют не найдена в базе данных");
        }

        return exchangeRateDao.saveExchangeRate(from, to, exchangeRate);
    }

    public List<ExchangeRateDto> getExchangeRates() {
        List<ExchangeRate> exchangeRatesList = exchangeRateDao.findAll();

        if (exchangeRatesList == null || exchangeRatesList.isEmpty()) {
            return new ArrayList<>();
        }

        return ExchangeRateMapper.toDto(exchangeRatesList, currencyDao);
    }

    public ExchangeRateDto getExchangeRate(List<CurrencyDto> currencies) {
        ExchangeRate rate = exchangeRateDao.findById(currencies.get(0).getId(), currencies.get(1).getId());

        if (rate == null) {
            throw new RuntimeException("Exchange rate not found for given currencies.");
        }

        return ExchangeRateMapper.toDto(rate, currencyDao);
    }
}
