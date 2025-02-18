package org.myapp.dao;

import org.myapp.model.Currency;

import java.util.List;

public interface CurrenciesDao {

    int save(Currency currency);
    Currency findById(int id);
    List<Currency> findAll();

    Currency findByCode(String code);
}