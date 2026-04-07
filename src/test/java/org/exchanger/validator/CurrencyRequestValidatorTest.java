package org.exchanger.validator;

import org.exchanger.dto.request.CurrencyRequest;
import org.exchanger.exception.ValidationException;
import org.exchanger.validator.impl.CurrencyRequestValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CurrencyRequestValidatorTest {

    private final CurrencyRequestValidator validator = new CurrencyRequestValidator();

    @Test
    void shouldValidateCorrectRequest() {
        CurrencyRequest request = new CurrencyRequest("Euro", "EUR", "€");

        assertDoesNotThrow(() -> validator.validate(request));
    }

    @Test
    void shouldThrowWhenNameIsBlank() {
        CurrencyRequest request = new CurrencyRequest("     ", "EUR", "€");

        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validate(request)
        );

        assertEquals("Field 'name' required", exception.getMessage());
    }
}
