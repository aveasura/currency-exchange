package org.exchanger.service;

import org.exchanger.dto.request.ExchangeRequest;
import org.exchanger.dto.response.ExchangeResponse;

public interface ExchangeService {
    ExchangeResponse convert(ExchangeRequest request);
}
