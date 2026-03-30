package org.exchanger.validator;

import org.exchanger.exception.BadRequestException;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public abstract class AbstractRequestValidator<T> implements RequestValidator<T> {

    private static final String CODE_PATTERN = "[A-Z]{3}";

    private static final Pattern NUMBER_PATTERN =
            Pattern.compile("^(0|[1-9]\\d*)(\\.\\d+)?$");

    private static final String INVALID_CODE_MESSAGE =
            "Currency code should contain exactly 3 latin letters. Example: EUR";

    protected void validateCodesAndPositiveNumber(String base, String target, String rawNumber) {
        validateCode(base);
        validateCode(target);

        if (base.equals(target)) {
            throw new BadRequestException("Same currencies selected");
        }

        validateNumericValue(rawNumber);
    }

    protected void validateCode(String code) {
        if (code == null || !code.matches(CODE_PATTERN)) {
            throw new BadRequestException(INVALID_CODE_MESSAGE);
        }
    }

    protected void validateNumericValue(String rawNumber) {
        if (rawNumber == null || rawNumber.isBlank()) {
            throw new BadRequestException("Number is missing");
        }

        String number = rawNumber.trim();

        if (!NUMBER_PATTERN.matcher(number).matches()) {
            throw new BadRequestException("Invalid number format. Example: 0.86 or 1");
        }

        BigDecimal result = new BigDecimal(number);
        if (result.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Number must be greater than zero");
        }

        if (result.scale() > 6) {
            throw new BadRequestException("Number scale exceeded. Max scale = 6. Example: 0.123456");
        }
    }
}
