package org.exchanger.service;

import org.exchanger.dto.request.ExchangeRateRequest;
import org.exchanger.dto.response.ExchangeRateResponse;
import org.exchanger.exception.ExchangeRateNotFoundException;
import org.exchanger.mapper.ResponseMapper;
import org.exchanger.model.Currency;
import org.exchanger.model.ExchangeRate;
import org.exchanger.repository.CurrencyRepository;
import org.exchanger.repository.ExchangeRateRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateService extends AbstractCurrencyService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final ResponseMapper<ExchangeRate, ExchangeRateResponse> responseMapper;

    public ExchangeRateService(CurrencyRepository currencyRepository,
                               ExchangeRateRepository exchangeRateRepository,
                               ResponseMapper<ExchangeRate, ExchangeRateResponse> responseMapper) {
        super(currencyRepository);
        this.exchangeRateRepository = exchangeRateRepository;
        this.responseMapper = responseMapper;
    }

    public List<ExchangeRateResponse> getAll() {
        List<ExchangeRate> exchangeRates = exchangeRateRepository.findAll();

        List<ExchangeRateResponse> response = new ArrayList<>();
        for (ExchangeRate rate : exchangeRates) {
            ExchangeRateResponse dto = responseMapper.toDto(rate);
            response.add(dto);
        }

        return response;
    }

    // todo parser
    public ExchangeRateResponse get(String pair) {
        String baseCurrencyCode = pair.substring(0, 3);
        String targetCurrencyCode = pair.substring(3, 6);

        Currency base = getCurrency(baseCurrencyCode);
        Currency target = getCurrency(targetCurrencyCode);

        ExchangeRate exchangeRate = exchangeRateRepository.find(base.getId(), target.getId())
                .orElseThrow(() -> new ExchangeRateNotFoundException(base.getCode(), target.getCode()));

        return responseMapper.toDto(exchangeRate);
    }

    public ExchangeRateResponse addExchangeRate(ExchangeRateRequest request) {
        Currency base = getCurrency(request.baseCurrencyCode());
        Currency target = getCurrency(request.targetCurrencyCode());
        BigDecimal rate = new BigDecimal(request.rate());

        ExchangeRate exchangeRate = new ExchangeRate(base, target, rate);

        Long createdId = exchangeRateRepository.create(base, target, rate);
        exchangeRate.setId(createdId);

        return responseMapper.toDto(exchangeRate);
    }
}
