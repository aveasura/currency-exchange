package org.myapp.dao;

import org.myapp.model.Currency;
import org.myapp.model.ExchangeRate;

import java.math.BigDecimal;
import java.util.List;

public interface ExchangeRateDao {

    int saveExchangeRate(Currency currencyFrom, Currency currencyTo, BigDecimal exchangeRate);

    List<ExchangeRate> findAll();
}
