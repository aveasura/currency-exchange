package org.exchanger.validator;

public interface RequestValidator<T> {
    void validate(T request);
}
