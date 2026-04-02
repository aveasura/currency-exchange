package org.exchanger.service.impl;

import org.exchanger.dto.request.ExchangeRequest;
import org.exchanger.dto.response.CurrencyResponse;
import org.exchanger.dto.response.ExchangeResponse;
import org.exchanger.exception.InvalidExchangeRequestException;
import org.exchanger.exception.ExchangeRateNotFoundException;
import org.exchanger.mapper.ResponseMapper;
import org.exchanger.model.Currency;
import org.exchanger.model.ExchangeRate;
import org.exchanger.repository.CurrencyRepository;
import org.exchanger.repository.ExchangeRateRepository;
import org.exchanger.service.ExchangeService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public final class DefaultExchangeService extends AbstractCurrencyLookupService implements ExchangeService {

    private static final String CROSS_RATE_CURRENCY_CODE = "USD";
    private static final int AMOUNT_SCALE = 2;
    private static final int RESPONSE_RATE_SCALE = 6;

    private static final int INTERNAL_RATE_SCALE = 30;

    private final ExchangeRateRepository exchangeRateRepository;
    private final ResponseMapper<Currency, CurrencyResponse> currencyResponseMapper;

    public DefaultExchangeService(CurrencyRepository currencyRepository,
                                  ExchangeRateRepository exchangeRateRepository,
                                  ResponseMapper<Currency, CurrencyResponse> currencyResponseMapper) {
        super(currencyRepository);
        this.exchangeRateRepository = exchangeRateRepository;
        this.currencyResponseMapper = currencyResponseMapper;
    }

    @Override
    public ExchangeResponse convert(ExchangeRequest request) {
        Currency base = getCurrency(request.from());
        Currency target = getCurrency(request.to());
        BigDecimal amount = new BigDecimal(request.amount());

        BigDecimal calculatedRate = resolveRate(base, target);
        BigDecimal responseRate = calculatedRate.setScale(RESPONSE_RATE_SCALE, RoundingMode.HALF_UP);
        BigDecimal convertedAmount = amount.multiply(calculatedRate)
                .setScale(AMOUNT_SCALE, RoundingMode.HALF_UP);

        CurrencyResponse baseCurrencyDto = currencyResponseMapper.toDto(base);
        CurrencyResponse targetCurrencyDto = currencyResponseMapper.toDto(target);

        return new ExchangeResponse(
                baseCurrencyDto,
                targetCurrencyDto,
                responseRate,
                amount,
                convertedAmount
        );
    }

    private BigDecimal resolveRate(Currency base, Currency target) {
        if (base.code().equals(target.code())) {
            throw new InvalidExchangeRequestException("Same currencies selected");
        }

        return findDirectOrReverseRate(base, target)
                .orElseGet(() -> findCrossRate(base, target));
    }

    private BigDecimal findCrossRate(Currency base, Currency target) {
        Currency usd = getCurrency(CROSS_RATE_CURRENCY_CODE);

        try {
            BigDecimal usdToBase = resolveUsdRate(usd, base);
            BigDecimal usdToTarget = resolveUsdRate(usd, target);

            if (usdToBase.compareTo(BigDecimal.ZERO) == 0) {
                throw new InvalidExchangeRequestException("Exchange rate is too small to calculate");
            }

            return usdToTarget.divide(usdToBase, INTERNAL_RATE_SCALE, RoundingMode.HALF_UP);
        } catch (ExchangeRateNotFoundException e) {
            throw new ExchangeRateNotFoundException(base.code(), target.code());
        }
    }

    private BigDecimal resolveUsdRate(Currency usd, Currency currency) {
        if (usd.code().equals(currency.code())) {
            return BigDecimal.ONE;
        }

        return findDirectOrReverseRate(usd, currency)
                .orElseThrow(() -> new ExchangeRateNotFoundException(usd.code(), currency.code()));
    }

    private Optional<BigDecimal> findDirectOrReverseRate(Currency base, Currency target) {
        Optional<ExchangeRate> direct =
                exchangeRateRepository.findByBaseCurrencyIdAndTargetCurrencyId(base.id(), target.id());

        if (direct.isPresent()) {
            return direct.map(ExchangeRate::rate);
        }

        Optional<ExchangeRate> reverse =
                exchangeRateRepository.findByBaseCurrencyIdAndTargetCurrencyId(target.id(), base.id());

        return reverse.map(rate ->
                BigDecimal.ONE.divide(rate.rate(), INTERNAL_RATE_SCALE, RoundingMode.HALF_UP));
    }
}