package org.exchanger.service;

import org.exchanger.dto.request.ExchangeRequest;
import org.exchanger.dto.response.CurrencyResponse;
import org.exchanger.dto.response.ExchangeResponse;
import org.exchanger.exception.ExchangeRateNotFoundException;
import org.exchanger.mapper.ResponseMapper;
import org.exchanger.model.Currency;
import org.exchanger.model.ExchangeRate;
import org.exchanger.repository.CurrencyRepository;
import org.exchanger.repository.ExchangeRateRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class ExchangeService extends AbstractCurrencyService {

    // According to specification, cross-rate is calculated only through USD.
    private static final String CROSS_RATE_CURRENCY_CODE = "USD";
    private static final int SCALE = 6;

    private final ExchangeRateRepository exchangeRateRepository;
    private final ResponseMapper<Currency, CurrencyResponse> currencyResponseMapper;

    public ExchangeService(CurrencyRepository currencyRepository,
                           ExchangeRateRepository exchangeRateRepository,
                           ResponseMapper<Currency, CurrencyResponse> currencyResponseMapper) {
        super(currencyRepository);
        this.exchangeRateRepository = exchangeRateRepository;
        this.currencyResponseMapper = currencyResponseMapper;
    }

    public ExchangeResponse convert(ExchangeRequest request) {
        Currency base = getCurrency(request.from());
        Currency target = getCurrency(request.to());
        BigDecimal amount = new BigDecimal(request.amount());

        BigDecimal rate = resolveRate(base, target);
        BigDecimal convertedAmount = amount.multiply(rate);

        CurrencyResponse baseCurrencyDto = currencyResponseMapper.toDto(base);
        CurrencyResponse targetCurrencyDto = currencyResponseMapper.toDto(target);

        return new ExchangeResponse(
                baseCurrencyDto,
                targetCurrencyDto,
                rate,
                amount,
                convertedAmount);
    }

    private BigDecimal resolveRate(Currency base, Currency target) {
        if (base.getCode().equals(target.getCode())) {
            return BigDecimal.ONE;
        }

        return findDirectOrReverseRate(base, target)
                .orElseGet(() -> findCrossRate(base, target));
    }

    private BigDecimal findCrossRate(Currency base, Currency target) {
        Currency usd = getCurrency(CROSS_RATE_CURRENCY_CODE);

        BigDecimal usdToBase = resolveUsdRate(usd, base);
        BigDecimal usdToTarget = resolveUsdRate(usd, target);

        return usdToTarget.divide(usdToBase, SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal resolveUsdRate(Currency usd, Currency currency) {
        if (usd.getCode().equals(currency.getCode())) {
            return BigDecimal.ONE;
        }

        return findDirectOrReverseRate(usd, currency)
                .orElseThrow(() -> new ExchangeRateNotFoundException(usd.getCode(), currency.getCode()));
    }

    private Optional<BigDecimal> findDirectOrReverseRate(Currency base, Currency target) {
        Optional<ExchangeRate> direct =
                exchangeRateRepository.findByBaseCurrencyIdAndTargetCurrencyId(base.getId(), target.getId());

        if (direct.isPresent()) {
            return direct.map(ExchangeRate::getRate);
        }

        Optional<ExchangeRate> reverse =
                exchangeRateRepository.findByBaseCurrencyIdAndTargetCurrencyId(target.getId(), base.getId());

        return reverse.map(rate ->
                BigDecimal.ONE.divide(rate.getRate(), SCALE, RoundingMode.HALF_UP));
    }
}
