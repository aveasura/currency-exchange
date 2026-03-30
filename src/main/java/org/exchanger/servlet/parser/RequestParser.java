package org.exchanger.servlet.parser;

import jakarta.servlet.http.HttpServletRequest;

public interface RequestParser<T> {
    T parse(HttpServletRequest request);
}
