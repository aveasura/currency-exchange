package org.exchanger.service.impl;

import org.exchanger.dto.request.ExchangeRateRequest;
import org.exchanger.dto.request.UpdateExchangeRateRequest;
import org.exchanger.dto.response.CurrencyResponse;
import org.exchanger.dto.response.ExchangeRateResponse;
import org.exchanger.dto.response.UpdateExchangeRateResponse;
import org.exchanger.exception.DuplicateEntityException;
import org.exchanger.exception.ExchangeRateAlreadyExistsException;
import org.exchanger.exception.ExchangeRateNotFoundException;
import org.exchanger.mapper.ResponseMapper;
import org.exchanger.model.Currency;
import org.exchanger.model.ExchangeRate;
import org.exchanger.repository.CurrencyRepository;
import org.exchanger.repository.ExchangeRateRepository;
import org.exchanger.service.ExchangeRateService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class DefaultExchangeRateService extends AbstractCurrencyLookupService implements ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final ResponseMapper<ExchangeRate, ExchangeRateResponse> responseMapper;

    public DefaultExchangeRateService(CurrencyRepository currencyRepository,
                                      ExchangeRateRepository exchangeRateRepository,
                                      ResponseMapper<ExchangeRate, ExchangeRateResponse> responseMapper) {
        super(currencyRepository);
        this.exchangeRateRepository = exchangeRateRepository;
        this.responseMapper = responseMapper;
    }

    @Override
    public List<ExchangeRateResponse> getAll() {
        List<ExchangeRate> exchangeRates = exchangeRateRepository.findAll();

        List<ExchangeRateResponse> response = new ArrayList<>();
        for (ExchangeRate rate : exchangeRates) {
            ExchangeRateResponse dto = responseMapper.toDto(rate);
            response.add(dto);
        }

        return response;
    }

    @Override
    public ExchangeRateResponse get(String baseCurrencyCode, String targetCurrencyCode) {
        Currency base = getCurrency(baseCurrencyCode);
        Currency target = getCurrency(targetCurrencyCode);

        ExchangeRate exchangeRate = exchangeRateRepository.findByBaseCurrencyIdAndTargetCurrencyId(base.getId(), target.getId())
                .orElseThrow(() -> new ExchangeRateNotFoundException(base.getCode(), target.getCode()));

        return responseMapper.toDto(exchangeRate);
    }

    @Override
    public ExchangeRateResponse create(ExchangeRateRequest request) {
        Currency base = getCurrency(request.baseCurrencyCode());
        Currency target = getCurrency(request.targetCurrencyCode());
        BigDecimal rate = new BigDecimal(request.rate());

        ExchangeRate exchangeRate = new ExchangeRate(base, target, rate);

        try {
            Long createdId = exchangeRateRepository.create(base.getId(), target.getId(), rate);
            exchangeRate.setId(createdId);
        } catch (DuplicateEntityException e) {
            throw new ExchangeRateAlreadyExistsException(base.getCode(), target.getCode());
        }

        return responseMapper.toDto(exchangeRate);
    }

    @Override
    public UpdateExchangeRateResponse updateExchangeRate(UpdateExchangeRateRequest request) {
        Currency base = getCurrency(request.baseCurrencyCode());
        Currency target = getCurrency(request.targetCurrencyCode());
        BigDecimal rate = new BigDecimal(request.rate());

        ExchangeRate exchangeRate = exchangeRateRepository.findByBaseCurrencyIdAndTargetCurrencyId(base.getId(), target.getId())
                .orElseThrow(() -> new ExchangeRateNotFoundException(base.getCode(), target.getCode()));

        exchangeRateRepository.updateRateById(exchangeRate.getId(), rate);
        exchangeRate.setRate(rate);

        return toDto(base, target, exchangeRate, rate);
    }

    private UpdateExchangeRateResponse toDto(Currency base,
                                             Currency target,
                                             ExchangeRate exchangeRate,
                                             BigDecimal rate) {
        CurrencyResponse baseDto = new CurrencyResponse(
                base.getId(),
                base.getFullName(),
                base.getCode(),
                base.getSign()
        );

        CurrencyResponse targetDto = new CurrencyResponse(
                target.getId(),
                target.getFullName(),
                target.getCode(),
                target.getSign()
        );

        return new UpdateExchangeRateResponse(
                exchangeRate.getId(),
                baseDto,
                targetDto,
                rate
        );
    }
}
