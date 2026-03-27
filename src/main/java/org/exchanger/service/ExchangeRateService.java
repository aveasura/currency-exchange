package org.exchanger.service;

import org.exchanger.dto.response.CreateCurrencyResponse;
import org.exchanger.dto.response.ExchangeRateResponse;
import org.exchanger.model.Currency;
import org.exchanger.model.ExchangeRate;
import org.exchanger.repository.CurrencyRepository;
import org.exchanger.repository.ExchangeRateRepository;

import java.util.ArrayList;
import java.util.List;

public class ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final CurrencyRepository currencyRepository;

    public ExchangeRateService(ExchangeRateRepository exchangeRateRepository, CurrencyRepository currencyRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
        this.currencyRepository = currencyRepository;
    }

    public List<ExchangeRateResponse> getAll() {
        List<ExchangeRate> exchangeRates = exchangeRateRepository.findAll();

        // todo mapper
        List<ExchangeRateResponse> exchangeRatesDto = new ArrayList<>();
        for (ExchangeRate rate : exchangeRates) {
            CreateCurrencyResponse baseCurrencyDto = new CreateCurrencyResponse(
                    rate.getBaseCurrency().getId(),
                    rate.getBaseCurrency().getFullName(),
                    rate.getBaseCurrency().getCode(),
                    rate.getBaseCurrency().getSign()
            );

            CreateCurrencyResponse targetCurrencyDto = new CreateCurrencyResponse(
                    rate.getTargetCurrency().getId(),
                    rate.getTargetCurrency().getFullName(),
                    rate.getTargetCurrency().getCode(),
                    rate.getTargetCurrency().getSign()
            );

            ExchangeRateResponse exchangeRateDto = new ExchangeRateResponse(
                    rate.getId(),
                    baseCurrencyDto,
                    targetCurrencyDto,
                    rate.getRate()
            );

            exchangeRatesDto.add(exchangeRateDto);
        }

        return exchangeRatesDto;
    }

    // todo парсим по 3 символа в две карренси
    public ExchangeRateResponse get(String pair) {
        String baseCurrencyCode = pair.substring(0, 3);
        String targetCurrencyCode = pair.substring(3, 6);

        Currency base = currencyRepository.findCurrency(baseCurrencyCode);
        Currency target = currencyRepository.findCurrency(targetCurrencyCode);
        ExchangeRate exchangeRate = exchangeRateRepository.find(base.getId(), target.getId());

        // todo mapper
        CreateCurrencyResponse baseCurrencyDto = new CreateCurrencyResponse(
                base.getId(),
                base.getFullName(),
                base.getCode(),
                base.getSign()
        );

        CreateCurrencyResponse targetCurrencyDto = new CreateCurrencyResponse(
                target.getId(),
                target.getFullName(),
                target.getCode(),
                target.getSign()
        );

        ExchangeRateResponse exchangeRateDto = new ExchangeRateResponse(
                exchangeRate.getId(),
                baseCurrencyDto,
                targetCurrencyDto,
                exchangeRate.getRate()
        );

        return exchangeRateDto;
    }
}
