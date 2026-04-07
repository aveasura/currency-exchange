package org.exchanger.service.impl;

import org.exchanger.dto.response.ExchangeRateResponse;
import org.exchanger.dto.response.UpdateExchangeRateResponse;
import org.exchanger.exception.ConflictException;
import org.exchanger.exception.CurrencyNotFoundException;
import org.exchanger.exception.ExchangeRateAlreadyExistsException;
import org.exchanger.exception.ExchangeRateNotFoundException;
import org.exchanger.mapper.ResponseMapper;
import org.exchanger.model.Currency;
import org.exchanger.model.ExchangeRate;
import org.exchanger.repository.CurrencyRepository;
import org.exchanger.repository.ExchangeRateRepository;
import org.exchanger.service.ExchangeRateService;
import org.exchanger.service.command.CreateExchangeRateCommand;
import org.exchanger.service.command.UpdateExchangeRateCommand;

import java.util.ArrayList;
import java.util.List;

public final class DefaultExchangeRateService extends AbstractCurrencyLookupService implements ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final ResponseMapper<ExchangeRate, ExchangeRateResponse> exchangeRateMapper;
    private final ResponseMapper<ExchangeRate, UpdateExchangeRateResponse> updateExchangeRateMapper;

    public DefaultExchangeRateService(CurrencyRepository currencyRepository,
                                      ExchangeRateRepository exchangeRateRepository,
                                      ResponseMapper<ExchangeRate, ExchangeRateResponse> exchangeRateMapper,
                                      ResponseMapper<ExchangeRate, UpdateExchangeRateResponse> updateExchangeRateMapper) {
        super(currencyRepository);
        this.exchangeRateRepository = exchangeRateRepository;
        this.exchangeRateMapper = exchangeRateMapper;
        this.updateExchangeRateMapper = updateExchangeRateMapper;
    }

    @Override
    public List<ExchangeRateResponse> getAll() {
        List<ExchangeRate> exchangeRates = exchangeRateRepository.findAll();

        List<ExchangeRateResponse> response = new ArrayList<>(exchangeRates.size());
        for (ExchangeRate rate : exchangeRates) {
            ExchangeRateResponse dto = exchangeRateMapper.toDto(rate);
            response.add(dto);
        }

        return response;
    }

    @Override
    public ExchangeRateResponse get(String baseCurrencyCode, String targetCurrencyCode) {
        try {
            Currency base = getCurrency(baseCurrencyCode);
            Currency target = getCurrency(targetCurrencyCode);

            ExchangeRate exchangeRate = exchangeRateRepository
                    .findByBaseCurrencyIdAndTargetCurrencyId(base.id(), target.id())
                    .orElseThrow(() -> new ExchangeRateNotFoundException(base.code(), target.code()));

            return exchangeRateMapper.toDto(exchangeRate);
        } catch (CurrencyNotFoundException e) {
            throw new ExchangeRateNotFoundException(baseCurrencyCode, targetCurrencyCode);
        }
    }

    @Override
    public ExchangeRateResponse create(CreateExchangeRateCommand command) {
        Currency base = getCurrency(command.baseCurrencyCode());
        Currency target = getCurrency(command.targetCurrencyCode());

        try {
            Long id = exchangeRateRepository.create(base.id(), target.id(), command.rate());
            ExchangeRate savedExchangeRate = new ExchangeRate(id, base, target, command.rate());

            return exchangeRateMapper.toDto(savedExchangeRate);
        } catch (ConflictException e) {
            throw new ExchangeRateAlreadyExistsException(base.code(), target.code(), e);
        }
    }

    @Override
    public UpdateExchangeRateResponse updateExchangeRate(UpdateExchangeRateCommand command) {
        try {
            Currency base = getCurrency(command.baseCurrencyCode());
            Currency target = getCurrency(command.targetCurrencyCode());

            ExchangeRate exchangeRate = exchangeRateRepository
                    .findByBaseCurrencyIdAndTargetCurrencyId(base.id(), target.id())
                    .orElseThrow(() -> new ExchangeRateNotFoundException(base.code(), target.code()));

            exchangeRateRepository.updateRateById(exchangeRate.id(), command.rate());
            ExchangeRate updatedExchangeRate = new ExchangeRate(exchangeRate.id(), base, target, command.rate());

            return updateExchangeRateMapper.toDto(updatedExchangeRate);
        } catch (CurrencyNotFoundException e) {
            throw new ExchangeRateNotFoundException(command.baseCurrencyCode(), command.targetCurrencyCode());
        }
    }
}
