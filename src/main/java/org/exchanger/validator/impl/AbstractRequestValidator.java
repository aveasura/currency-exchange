package org.exchanger.validator.impl;

import org.exchanger.exception.ValidationException;
import org.exchanger.validator.RequestValidator;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public abstract class AbstractRequestValidator<T> implements RequestValidator<T> {

    private static final int MAX_NUMBER_LENGTH = 30;
    private static final int MAX_DECIMAL_SCALE = 6;

    private static final BigDecimal MAX_RATE = new BigDecimal("999999.999999");

    private static final String CODE_PATTERN = "[A-Z]{3}";

    private static final String INVALID_CODE_MESSAGE =
            "Currency code should contain exactly 3 latin letters. Example: EUR";

    private static final String INVALID_NUMBER_MESSAGE =
            "Invalid number format. Example: 0.86 or 1";

    private static final String SCALE_EXCEEDED_MESSAGE =
            "Number scale exceeded. Max scale = 6. Example: 0.123456";

    private static final String RATE_TOO_LARGE_MESSAGE =
            "Exchange rate should not exceed 999999.999999";

    private static final Pattern NUMBER_PATTERN =
            Pattern.compile("^\\d+(\\.\\d+)?$");

    protected void validateCodesAndRate(String base, String target, String rawRate) {
        validateCode(base);
        validateCode(target);

        if (base.equals(target)) {
            throw new ValidationException("Same currencies selected");
        }

        validateRate(rawRate);
    }

    protected void validateCodesAndAmount(String base, String target, String rawAmount) {
        validateCode(base);
        validateCode(target);

        if (base.equals(target)) {
            throw new ValidationException("Same currencies selected");
        }

        validateAmount(rawAmount);
    }

    protected void validateCode(String code) {
        if (code == null || !code.matches(CODE_PATTERN)) {
            throw new ValidationException(INVALID_CODE_MESSAGE);
        }
    }

    protected void validateRate(String rawRate) {
        BigDecimal rate = validatePositiveNumber(rawRate);

        if (rate.compareTo(MAX_RATE) > 0) {
            throw new ValidationException(RATE_TOO_LARGE_MESSAGE);
        }
    }

    protected void validateAmount(String rawAmount) {
        validatePositiveNumber(rawAmount);
    }

    private BigDecimal validatePositiveNumber(String rawNumber) {
        if (rawNumber == null || rawNumber.isBlank()) {
            throw new ValidationException("Number is missing");
        }

        String number = rawNumber.trim();

        if (number.length() > MAX_NUMBER_LENGTH) {
            throw new ValidationException("Number is too long");
        }

        if (!NUMBER_PATTERN.matcher(number).matches()) {
            throw new ValidationException(INVALID_NUMBER_MESSAGE);
        }

        validateFraction(number);

        BigDecimal result = new BigDecimal(number);
        if (result.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Number must be greater than zero");
        }

        return result;
    }

    private void validateFraction(String number) {
        int dotIndex = number.indexOf('.');
        if (dotIndex < 0) {
            return;
        }

        String fraction = number.substring(dotIndex + 1);
        if (fraction.length() <= MAX_DECIMAL_SCALE) {
            return;
        }

        String extraFraction = fraction.substring(MAX_DECIMAL_SCALE);
        boolean onlyZerosAfterScale = extraFraction.chars().allMatch(ch -> ch == '0');

        if (onlyZerosAfterScale) {
            throw new ValidationException(INVALID_NUMBER_MESSAGE);
        }

        throw new ValidationException(SCALE_EXCEEDED_MESSAGE);
    }
}