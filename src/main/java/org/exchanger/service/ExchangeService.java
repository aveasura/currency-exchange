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
        BigDecimal amount = request.amount();

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

        Optional<ExchangeRate> directRate = exchangeRateRepository.findByBaseCurrencyIdAndTargetCurrencyId(base.getId(), target.getId());
        if (directRate.isPresent()) {
            return directRate.get().getRate();
        }

        Optional<ExchangeRate> reverseRate = exchangeRateRepository.findByBaseCurrencyIdAndTargetCurrencyId(target.getId(), base.getId());

        return reverseRate.map(exchangeRate -> BigDecimal.ONE.divide(exchangeRate.getRate(), 6, RoundingMode.HALF_UP))
                .orElseGet(() -> findCrossRate(base, target));
    }

    private BigDecimal findCrossRate(Currency base, Currency target) {
        Currency usd = getCurrency(CROSS_RATE_CURRENCY_CODE);

        Optional<ExchangeRate> usdToBase = exchangeRateRepository.findByBaseCurrencyIdAndTargetCurrencyId(usd.getId(), base.getId());
        Optional<ExchangeRate> usdToTarget = exchangeRateRepository.findByBaseCurrencyIdAndTargetCurrencyId(usd.getId(), target.getId());

        if (usdToBase.isPresent() && usdToTarget.isPresent()) {
            return usdToTarget.get().getRate()
                    .divide(usdToBase.get().getRate(), 6, RoundingMode.HALF_UP);
        }

        throw new ExchangeRateNotFoundException(base.getCode(), target.getCode());
    }
}
