package org.myapp.service;

import org.myapp.dao.CurrencyDao;
import org.myapp.dto.CurrencyDto;
import org.myapp.dto.ExchangeRateDto;
import org.myapp.error.OperationResult;
import org.myapp.mapper.CurrencyMapper;
import org.myapp.model.Currency;
import org.myapp.model.ExchangeRate;

import java.util.ArrayList;
import java.util.List;

public class CurrenciesService {

    private final CurrencyDao dao;

    public CurrenciesService(CurrencyDao dao) {
        this.dao = dao;
    }

    public CurrencyDto createDto(String code, String name, String sign) {
        return new CurrencyDto(code, name, sign);
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

    public OperationResult patchCurrency(String code, CurrencyDto dto) {

        Currency currency = findByIdOrCode(code);
        if (currency == null) {
            return new OperationResult(false, "Currency not found");
        }

        if (dto.getFullName() != null) {
            currency.setFullName(dto.getFullName());
        }

        if (dto.getSign() != null) {
            currency.setSign(dto.getSign());
        }

        dao.update(currency);
        return new OperationResult(true, "Currency successfully updated");
    }

    public List<CurrencyDto> getCurrenciesByPath(String path) {
        List<Currency> currencies = new ArrayList<>();

        String baseCurrencyCode = path.substring(0, 3);
        String targetCurrencyCode = path.substring(3, 6);

        // Чек на существование валют в бд
        Currency from = findByIdOrCode(baseCurrencyCode);
        Currency to = findByIdOrCode(targetCurrencyCode);

        if (from == null || to == null) {
            throw new IllegalArgumentException("One or both currencies not found: " + baseCurrencyCode + ", " + targetCurrencyCode);
        }

        currencies.add(from);
        currencies.add(to);

        return CurrencyMapper.toDto(currencies);
    }
}
