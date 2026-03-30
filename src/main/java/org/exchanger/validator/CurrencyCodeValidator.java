package org.exchanger.validator;

public class CurrencyCodeValidator extends AbstractRequestValidator<String> {
    @Override
    public void validate(String code) {
        validateCode(code);
    }
}
