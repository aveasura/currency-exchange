package org.myapp.mapper;

import org.myapp.dto.CurrencyDto;
import org.myapp.model.Currency;

import java.util.ArrayList;
import java.util.List;

public class CurrencyMapper {
    public static Currency toEntity(CurrencyDto dto) {
        return new Currency(dto.getCode(), dto.getFullName(), dto.getSign());
    }

    public static CurrencyDto toDto(Currency currency) {
        return new CurrencyDto(currency.getId(), currency.getCode(), currency.getFullName(), currency.getSign());
    }

    public static List<CurrencyDto> toDto(List<Currency> currencies) {
        List<CurrencyDto> dtoList = new ArrayList<>();
        for (Currency currency : currencies) {
            dtoList.add(toDto(currency));
        }
        return dtoList;
    }
}