package org.exchanger.service;

import org.exchanger.dto.request.ExchangeRateRequest;
import org.exchanger.dto.response.CurrencyResponse;
import org.exchanger.dto.response.ExchangeRateResponse;
import org.exchanger.exception.CurrencyNotFoundException;
import org.exchanger.exception.ExchangeRateNotFoundException;
import org.exchanger.model.Currency;
import org.exchanger.model.ExchangeRate;
import org.exchanger.repository.CurrencyRepository;
import org.exchanger.repository.ExchangeRateRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateService extends AbstractCurrencyService {

    private final ExchangeRateRepository exchangeRateRepository;

    public ExchangeRateService(CurrencyRepository currencyRepository, ExchangeRateRepository exchangeRateRepository) {
        super(currencyRepository);
        this.exchangeRateRepository = exchangeRateRepository;
    }

    public List<ExchangeRateResponse> getAll() {
        List<ExchangeRate> exchangeRates = exchangeRateRepository.findAll();

        // todo mapper
        List<ExchangeRateResponse> exchangeRatesDto = new ArrayList<>();
        for (ExchangeRate rate : exchangeRates) {
            CurrencyResponse baseCurrencyDto = new CurrencyResponse(
                    rate.getBaseCurrency().getId(),
                    rate.getBaseCurrency().getFullName(),
                    rate.getBaseCurrency().getCode(),
                    rate.getBaseCurrency().getSign()
            );

            CurrencyResponse targetCurrencyDto = new CurrencyResponse(
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

        Currency base = getCurrency(baseCurrencyCode);
        Currency target = getCurrency(targetCurrencyCode);

        ExchangeRate exchangeRate = exchangeRateRepository.find(base.getId(), target.getId());
        // todo mapper
        CurrencyResponse baseCurrencyDto = new CurrencyResponse(
                base.getId(),
                base.getFullName(),
                base.getCode(),
                base.getSign()
        );

        CurrencyResponse targetCurrencyDto = new CurrencyResponse(
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

    public ExchangeRateResponse addExchangeRate(ExchangeRateRequest dto) {
        Currency base = getCurrency(dto.baseCurrencyCode());
        Currency target = getCurrency(dto.targetCurrencyCode());
        BigDecimal rate = new BigDecimal(dto.rate());

        Long exchangeRateId = exchangeRateRepository.create(base, target, rate);

        // todo mapper
        CurrencyResponse baseCurrencyDto = new CurrencyResponse(
                base.getId(),
                base.getFullName(),
                base.getCode(),
                base.getSign()
        );

        CurrencyResponse targetCurrencyDto = new CurrencyResponse(
                target.getId(),
                target.getFullName(),
                target.getCode(),
                target.getSign()
        );

        ExchangeRateResponse exchangeRateDto = new ExchangeRateResponse(
                exchangeRateId,
                baseCurrencyDto,
                targetCurrencyDto,
                rate
        );

        return exchangeRateDto;
    }
}
