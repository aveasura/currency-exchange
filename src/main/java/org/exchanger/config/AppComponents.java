package org.exchanger.config;

import com.zaxxer.hikari.HikariDataSource;
import org.exchanger.service.CurrencyService;
import org.exchanger.service.ExchangeRateService;
import org.exchanger.service.ExchangeService;
import tools.jackson.databind.ObjectMapper;

public record AppComponents(
        HikariDataSource dataSource,
        CurrencyService currencyService,
        ExchangeRateService exchangeRateService,
        ExchangeService exchangeService,
        ObjectMapper objectMapper) {
}
