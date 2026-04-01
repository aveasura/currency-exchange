package org.exchanger.repository;

import org.exchanger.model.Currency;

public interface CurrencyRepository extends Repository<Currency>{
    Long create(String code, String fullName, String sign);

    Currency findByCode(String code);
}
