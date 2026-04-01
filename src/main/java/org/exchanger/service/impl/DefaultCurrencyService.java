package org.exchanger.service.impl;

import org.exchanger.dto.request.CurrencyRequest;
import org.exchanger.dto.response.CurrencyResponse;
import org.exchanger.exception.CurrencyAlreadyExistsException;
import org.exchanger.exception.DuplicateEntityException;
import org.exchanger.mapper.RequestMapper;
import org.exchanger.mapper.ResponseMapper;
import org.exchanger.model.Currency;
import org.exchanger.repository.CurrencyRepository;
import org.exchanger.service.CurrencyService;

import java.util.ArrayList;
import java.util.List;

public final class DefaultCurrencyService extends AbstractCurrencyLookupService implements CurrencyService {

    private final RequestMapper<CurrencyRequest, Currency> requestMapper;
    private final ResponseMapper<Currency, CurrencyResponse> responseMapper;

    public DefaultCurrencyService(CurrencyRepository currencyRepository,
                                  RequestMapper<CurrencyRequest, Currency> requestMapper,
                                  ResponseMapper<Currency, CurrencyResponse> responseMapper) {
        super(currencyRepository);
        this.requestMapper = requestMapper;
        this.responseMapper = responseMapper;
    }

    @Override
    public CurrencyResponse create(CurrencyRequest dto) {
        Currency currency = requestMapper.toEntity(dto);

        try {
            Long id = currencyRepository.create(currency.getCode(), currency.getFullName(), currency.getSign());
            currency.setId(id);
        } catch (DuplicateEntityException e) {
            throw new CurrencyAlreadyExistsException(currency.getCode(), e);
        }

        return responseMapper.toDto(currency);
    }

    @Override
    public CurrencyResponse getByCurrencyCode(String code) {
        Currency currency = getCurrency(code);
        return responseMapper.toDto(currency);
    }

    @Override
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
