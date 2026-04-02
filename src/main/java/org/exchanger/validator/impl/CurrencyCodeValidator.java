package org.exchanger.validator.impl;

public final class CurrencyCodeValidator extends AbstractRequestValidator<String> {
    @Override
    public void validate(String code) {
        validateCode(code);
    }
}
