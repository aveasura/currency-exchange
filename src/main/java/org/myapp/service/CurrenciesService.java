package org.myapp.service;

import org.myapp.dao.CurrenciesDao;
import org.myapp.dto.CurrencyDto;
import org.myapp.mapper.CurrencyMapper;
import org.myapp.model.Currency;

import java.util.List;

public class CurrenciesService {

    private final CurrenciesDao dao;

    public CurrenciesService(CurrenciesDao dao) {
        this.dao = dao;
    }

    public Currency addCurrency(CurrencyDto dto) {
        // Если данные некорректные, просто возвращаем null
        if (isDtoNullable(dto))
            return null;

        Currency currency = CurrencyMapper.toEntity(dto);
        int generatedId = dao.save(currency);
        if (generatedId > 0) {
            currency.setId(generatedId);
        }

        return currency;
    }

    public CurrencyDto getCurrency(String code) {
        Currency currency = findByIdOrCode(code);

        if (currency == null)
            return null;

        return CurrencyMapper.toDto(currency);
    }

    public List<CurrencyDto> getCurrencies() {
        // можно сделать проверку на пустоту списка итд
        List<Currency> currencies = dao.findAll();

        return CurrencyMapper.toDto(currencies);
    }

    // todo json boolean
    public boolean updateCurrency(CurrencyDto dto, String choose) {
        if (isDtoNullable(dto))
            return false;

        Currency currency = findByIdOrCode(choose);
        if (currency == null) {
            System.out.println("Валюта не найдена");
            return false; // потом будем возвращать bool чтобы контроллер давал json ответ "валюта не найдена"
        }

        // обновить поля которые переданы
        if (dto.getCode() != null && !dto.getCode().isEmpty()) {
            currency.setCode(dto.getCode());
        }

        if (dto.getFullName() != null && !dto.getFullName().isEmpty()) {
            currency.setFullName(dto.getFullName());
        }
        if (dto.getSign() != null && !dto.getSign().isEmpty()) {
            currency.setSign(dto.getSign());
        }

        dao.update(currency);
        return true;
    }

    public CurrencyDto createCurrencyDto(String code, String name, String sign) {
        return new CurrencyDto(code, name, sign);
    }

    private boolean isDtoNullable(CurrencyDto dto) {
        return dto == null || dto.getCode() == null || dto.getFullName() == null || dto.getSign() == null;
    }

    public boolean isJson(String acceptHeader) {
        return acceptHeader != null && acceptHeader.toLowerCase().contains("application/json");
    }

    public boolean isList(Object object) {
        return object instanceof List<?>;
    }

    public boolean isValidPath(String pathInfo) {
        return pathInfo != null && pathInfo.length() > 1;
    }

    private Currency findByIdOrCode(String code) {
        Currency currency;
        if (code.matches("\\d+")) {
            int id = Integer.parseInt(code);
            currency = dao.findById(id);
        } else {
            currency = dao.findByCode(code);
        }
        return currency;
    }
}
