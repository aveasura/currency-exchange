package org.myapp.service;

import org.myapp.dao.CurrenciesDAO;
import org.myapp.model.Currency;

import java.util.List;

public class CurrenciesService {

    private final CurrenciesDAO dao;

    public CurrenciesService(CurrenciesDAO dao) {
        this.dao = dao;
    }

    public int addCurrency(Currency currency) {
        // можно сделать проверку на вернувшийся id
        return dao.addCurrency(currency);
    }

    public List<Currency> getCurrencies() {
        // можно сделать проверку на пустоту списка итд
        return dao.getCurrencies();
    }
}
