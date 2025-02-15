package org.myapp.mapper;

import org.myapp.dto.CurrencyDto;
import org.myapp.model.Currency;

public class CurrencyMapper {
    public static Currency toEntity(CurrencyDto dto) {
        return new Currency(dto.getCode(), dto.getFullName(), dto.getSign());
    }
}