package org.myapp.mapper;

import org.myapp.dao.CurrencyDao;
import org.myapp.dto.CurrencyDto;
import org.myapp.dto.ExchangeRateDto;
import org.myapp.model.Currency;
import org.myapp.model.ExchangeRate;

import java.util.ArrayList;
import java.util.List;

public class ExchangeRateMapper {

    public static ExchangeRateDto toDto(ExchangeRate exchangeRate, CurrencyDao currencyDao) {
        Currency baseCurrency = currencyDao.findById(exchangeRate.getBaseCurrencyId());
        Currency targetCurrency = currencyDao.findById(exchangeRate.getTargetCurrencyId());

        if (baseCurrency == null || targetCurrency == null) {
            throw new IllegalArgumentException("Invalid currency IDs in ExchangeRate");
        }

        CurrencyDto baseCurrencyDto = new CurrencyDto(
                baseCurrency.getId(),
                baseCurrency.getCode(),
                baseCurrency.getFullName(),
                baseCurrency.getSign());

        CurrencyDto targetCurrencyDto = new CurrencyDto(
                targetCurrency.getId(),
                targetCurrency.getCode(),
                targetCurrency.getFullName(),
                targetCurrency.getSign());

        return new ExchangeRateDto(exchangeRate.getId(), baseCurrencyDto, targetCurrencyDto, exchangeRate.getRate());
    }

    public static List<ExchangeRateDto> toDto(List<ExchangeRate> exchangeRates, CurrencyDao currencyDao) {
        List<ExchangeRateDto> dtoList = new ArrayList<>();
        for (ExchangeRate exchangeRate : exchangeRates) {
            dtoList.add(toDto(exchangeRate, currencyDao));
        }
        return dtoList;
    }
}