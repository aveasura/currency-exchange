package org.exchanger.servlet.parser;

import jakarta.servlet.http.HttpServletRequest;
import org.exchanger.exception.BadRequestException;

import java.util.Locale;

public abstract class AbstractRequestParser<T> implements RequestParser<T> {

    private static final String SINGLE_PATH_SEGMENT_PATTERN = "/[^/]+";

    protected static final int CURRENCY_CODE_LENGTH = 3;

    protected String getRequiredParameter(HttpServletRequest request, String name) {
        String value = request.getParameter(name);

        if (value == null || value.isBlank()) {
            throw new BadRequestException("Field '" + name + "' required");
        }

        return value.trim();
    }

    protected String getCleanPath(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.isBlank()) {
            throw new BadRequestException("Path variable is missing");
        }

        String cleanPath = pathInfo.trim();

        if (cleanPath.equals("/")) {
            throw new BadRequestException("Path variable is missing");
        }

        if (!cleanPath.matches(SINGLE_PATH_SEGMENT_PATTERN)) {
            throw new BadRequestException("Invalid path");
        }

        return cleanPath.substring(1);
    }

    protected String normalizeCode(String code) {
        return code.trim().toUpperCase(Locale.ROOT);
    }
}