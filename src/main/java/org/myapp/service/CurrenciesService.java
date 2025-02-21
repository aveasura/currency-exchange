package org.myapp.service;

import org.myapp.dao.Dao;
import org.myapp.dto.CurrencyDto;
import org.myapp.error.OperationResult;
import org.myapp.mapper.CurrencyMapper;
import org.myapp.model.Currency;

import java.util.ArrayList;
import java.util.List;

public class CurrenciesService {

    private final Dao<Currency> dao;

    public CurrenciesService(Dao<Currency> dao) {
        this.dao = dao;
    }

    public OperationResult addCurrency(CurrencyDto dto) {
        if (isDtoNullable(dto)) {
            return new OperationResult(false, "Invalid input");
        }

        Currency currency = CurrencyMapper.toEntity(dto);
        int generatedId = dao.save(currency);

        if (generatedId <= 0) {
            return new OperationResult(false, "Currency already exist");
        }

        currency.setId(generatedId);
        return new OperationResult(true, "Currency added successfully", CurrencyMapper.toDto(currency));
    }

    public OperationResult getCurrency(String code) {
        Currency currency = findByIdOrCode(code);

        if (currency == null) {
            return new OperationResult(false, "Currency not found or does not exist");
        }

        return new OperationResult(true, "Currency find", CurrencyMapper.toDto(currency));
    }

    public List<CurrencyDto> getCurrencies() {
        try {
            List<Currency> currencies = dao.findAll();

            if (currencies == null || currencies.isEmpty()) {
                return new ArrayList<>();
            }

            return CurrencyMapper.toDto(currencies);

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

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

    private boolean isDtoNullable(CurrencyDto dto) {
        return dto == null || dto.getCode() == null || dto.getFullName() == null || dto.getSign() == null;
    }

    public boolean isJson(String acceptHeader) {
        return acceptHeader != null && acceptHeader.toLowerCase().contains("application/json");
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
