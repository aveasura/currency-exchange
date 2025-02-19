package org.myapp.dao;

import org.myapp.model.Currency;

public interface CurrencyDao extends Dao<Currency> {
    Currency findByCode(String code);
}
