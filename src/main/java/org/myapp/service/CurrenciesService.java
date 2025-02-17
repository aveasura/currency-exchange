package org.myapp.service;

import org.myapp.dao.CurrenciesDaoImpl;
import org.myapp.dto.CurrencyDto;
import org.myapp.mapper.CurrencyMapper;
import org.myapp.model.Currency;

import java.util.List;

public class CurrenciesService {

    private final CurrenciesDaoImpl dao;

    public CurrenciesService(CurrenciesDaoImpl dao) {
        this.dao = dao;
    }

    public Currency addCurrency(CurrencyDto dto) {
        // Если данные некорректные, просто возвращаем null
        if (dto == null || dto.getCode() == null || dto.getFullName() == null || dto.getSign() == null) {
            return null;
        }

        Currency currency = CurrencyMapper.toEntity(dto);
        int generatedId = dao.save(currency);
        if (generatedId > 0) {
            currency.setId(generatedId);
        }

        return currency;
    }

    public CurrencyDto getCurrency(String currencyId) {
        int id = Integer.parseInt(currencyId);
        Currency currency = dao.findById(id);

        return CurrencyMapper.toDto(currency);
    }

    public List<CurrencyDto> getCurrencies() {
        // можно сделать проверку на пустоту списка итд
        List<Currency> currencies = dao.findAll();

        return CurrencyMapper.toDto(currencies);
    }

    public boolean isJson(String acceptHeader) {
        return acceptHeader != null && acceptHeader.toLowerCase().contains("application/json");
    }

    public boolean isList(Object object) {
        return object instanceof List<?>;
    }

    public CurrencyDto createCurrencyDto(String code, String name, String sign) {
        return new CurrencyDto(code, name, sign);
    }

    public boolean isValidPath(String pathInfo) {
        return pathInfo != null && pathInfo.length() > 1;
    }
}
