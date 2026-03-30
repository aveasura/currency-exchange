package org.exchanger.service;

import org.exchanger.dto.request.CurrencyRequest;
import org.exchanger.dto.response.CurrencyResponse;
import org.exchanger.exception.CurrencyAlreadyExistsException;
import org.exchanger.exception.DataAccessException;
import org.exchanger.exception.DuplicateEntityException;
import org.exchanger.mapper.RequestMapper;
import org.exchanger.mapper.ResponseMapper;
import org.exchanger.model.Currency;
import org.exchanger.repository.CurrencyRepository;

import java.util.ArrayList;
import java.util.List;

public class CurrencyService extends AbstractCurrencyService {

    private final RequestMapper<CurrencyRequest, Currency> requestMapper;
    private final ResponseMapper<Currency, CurrencyResponse> responseMapper;

    public CurrencyService(CurrencyRepository currencyRepository,
                           RequestMapper<CurrencyRequest, Currency> requestMapper,
                           ResponseMapper<Currency, CurrencyResponse> responseMapper) {
        super(currencyRepository);
        this.requestMapper = requestMapper;
        this.responseMapper = responseMapper;
    }

    public CurrencyResponse createCurrency(CurrencyRequest dto) {
        Currency currency = requestMapper.toEntity(dto);

        try {
            Long id = currencyRepository.create(currency.getCode(), currency.getFullName(), currency.getSign());
            currency.setId(id);
        } catch (DuplicateEntityException e) {
            throw new CurrencyAlreadyExistsException(currency.getCode());
        }

        return responseMapper.toDto(currency);
    }

    public CurrencyResponse get(String code) {
        Currency currency = getCurrency(code);
        return responseMapper.toDto(currency);
    }

    public List<CurrencyResponse> getAll() {
        List<Currency> currencies = currencyRepository.findAll();

        List<CurrencyResponse> responseDto = new ArrayList<>();
        for (Currency currency : currencies) {
            CurrencyResponse dto = responseMapper.toDto(currency);
            responseDto.add(dto);
        }

        return responseDto;
    }
}
