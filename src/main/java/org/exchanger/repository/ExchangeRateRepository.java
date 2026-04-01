package org.exchanger.repository;

import org.exchanger.model.ExchangeRate;

import java.math.BigDecimal;
import java.util.Optional;

public interface ExchangeRateRepository extends Repository<ExchangeRate> {
    Long create(Long baseCurrencyId, Long targetCurrencyId, BigDecimal rate);

    Optional<ExchangeRate> findByBaseCurrencyIdAndTargetCurrencyId(Long baseCurrencyId, Long targetCurrencyId);

    void updateRateById(Long exchangeRateId, BigDecimal rate);
}
