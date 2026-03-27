package org.exchanger.service;

import org.exchanger.dto.request.CurrencyRequest;
import org.exchanger.dto.response.CurrencyResponse;
import org.exchanger.model.Currency;
import org.exchanger.repository.CurrencyRepository;

import java.util.ArrayList;
import java.util.List;

public class CurrencyService extends AbstractCurrencyService {

    public CurrencyService(CurrencyRepository currencyRepository) {
        super(currencyRepository);
    }

    public CurrencyResponse get(String code) {
        Currency currency = getCurrency(code);

        //todo mapper
        CurrencyResponse dto =
                new CurrencyResponse(
                        currency.getId(),
                        currency.getFullName(),
                        currency.getCode(),
                        currency.getSign()
                );

        return dto;
    }

    public List<CurrencyResponse> getAll() {
        List<Currency> currencies = currencyRepository.findAll();

        // todo mapper
        List<CurrencyResponse> responseDto = new ArrayList<>();
        for (Currency currency : currencies) {
            CurrencyResponse dto = new CurrencyResponse(
                    currency.getId(),
                    currency.getFullName(),
                    currency.getCode(),
                    currency.getSign()
            );

            responseDto.add(dto);
        }

        return responseDto;
    }

    public CurrencyResponse createCurrency(CurrencyRequest dto) {
        Currency currency = new Currency(
                dto.name(),
                dto.code(),
                dto.sign()
        );

        Long currencyId = currencyRepository.create(currency);

        CurrencyResponse responseDto = new CurrencyResponse(
                currencyId,
                currency.getFullName(),
                currency.getCode(),
                currency.getSign()
        );

        return responseDto;
    }
}
