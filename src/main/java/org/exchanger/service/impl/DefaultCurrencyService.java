package org.exchanger.service.impl;

import org.exchanger.dto.request.CurrencyRequest;
import org.exchanger.dto.response.CurrencyResponse;
import org.exchanger.exception.ConflictException;
import org.exchanger.exception.CurrencyAlreadyExistsException;
import org.exchanger.mapper.ResponseMapper;
import org.exchanger.model.Currency;
import org.exchanger.repository.CurrencyRepository;
import org.exchanger.service.CurrencyService;

import java.util.ArrayList;
import java.util.List;

public final class DefaultCurrencyService extends AbstractCurrencyLookupService implements CurrencyService {

    private final ResponseMapper<Currency, CurrencyResponse> responseMapper;

    public DefaultCurrencyService(CurrencyRepository currencyRepository,
                                  ResponseMapper<Currency, CurrencyResponse> responseMapper) {
        super(currencyRepository);
        this.responseMapper = responseMapper;
    }

    @Override
    public CurrencyResponse create(CurrencyRequest dto) {
        try {
            Long id = currencyRepository.create(dto.code(), dto.name(), dto.sign());
            Currency savedCurrency = new Currency(id, dto.name(), dto.code(), dto.sign());

            return responseMapper.toDto(savedCurrency);
        } catch (ConflictException e) {
            throw new CurrencyAlreadyExistsException(dto.code(), e);
        }
    }

    @Override
    public CurrencyResponse getByCurrencyCode(String code) {
        Currency currency = getCurrency(code);
        return responseMapper.toDto(currency);
    }

    @Override
    public List<CurrencyResponse> getAll() {
        List<Currency> currencies = currencyRepository.findAll();

        List<CurrencyResponse> responseDto = new ArrayList<>(currencies.size());
        for (Currency currency : currencies) {
            CurrencyResponse dto = responseMapper.toDto(currency);
            responseDto.add(dto);
        }

        return responseDto;
    }
}
