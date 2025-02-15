package org.myapp.service;

import org.myapp.dao.CurrenciesDAO;
import org.myapp.dto.CurrencyDto;
import org.myapp.mapper.CurrencyMapper;
import org.myapp.model.Currency;

import java.util.List;

public class CurrenciesService {

    private final CurrenciesDAO dao;

    public CurrenciesService(CurrenciesDAO dao) {
        this.dao = dao;
    }

    public Currency addCurrency(CurrencyDto dto) {
        // Если данные некорректные, просто возвращаем null
        if (dto == null || dto.getCode() == null || dto.getFullName() == null || dto.getSign() == null) {
            return null;
        }

        Currency currency = CurrencyMapper.toEntity(dto);
        int generatedId = dao.addCurrency(currency);
        if (generatedId > 0) {
            currency.setId(generatedId);
        }

        return currency;
    }


    public List<Currency> getCurrencies() {
        // можно сделать проверку на пустоту списка итд
        return dao.getCurrencies();
    }
}
