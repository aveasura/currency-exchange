package org.exchanger.service;

import org.exchanger.dto.request.CreateCurrencyRequest;
import org.exchanger.dto.response.CreateCurrencyResponse;
import org.exchanger.model.Currency;
import org.exchanger.repository.CurrencyRepository;

import java.util.ArrayList;
import java.util.List;

public class CurrencyService extends AbstractCurrencyService {

    public CurrencyService(CurrencyRepository currencyRepository) {
        super(currencyRepository);
    }

    public CreateCurrencyResponse get(String code) {
        Currency currency = getCurrency(code);

        //todo mapper
        CreateCurrencyResponse dto =
                new CreateCurrencyResponse(currency.getId(),
                        currency.getFullName(),
                        currency.getCode(),
                        currency.getSign());

        return dto;
    }

    public List<CreateCurrencyResponse> getAll() {
        List<Currency> currencies = currencyRepository.findAll();

        // todo mapper
        List<CreateCurrencyResponse> responseDto = new ArrayList<>();
        for (Currency currency : currencies) {
            CreateCurrencyResponse dto
                    = new CreateCurrencyResponse(currency.getId(), currency.getFullName(), currency.getCode(), currency.getSign());

            responseDto.add(dto);
        }

        return responseDto;
    }

    public void createCurrency(CreateCurrencyRequest dto) {
        Currency currency = new Currency(dto.name(), dto.code(), dto.sign());

        currencyRepository.create(currency);
    }
}
