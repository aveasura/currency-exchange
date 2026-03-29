package org.exchanger.service;

import org.exchanger.dto.request.CurrencyRequest;
import org.exchanger.dto.response.CurrencyResponse;
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

        Long id = currencyRepository.create(currency);
        currency.setId(id);

        CurrencyResponse responseDto = responseMapper.toDto(currency);
        return responseDto;
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
