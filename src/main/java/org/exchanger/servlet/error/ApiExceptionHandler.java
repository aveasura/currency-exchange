package org.exchanger.servlet.error;

import jakarta.servlet.http.HttpServletResponse;
import org.exchanger.dto.response.ErrorResponse;
import org.exchanger.exception.AppException;
import org.exchanger.exception.ConflictException;
import org.exchanger.exception.EntityNotFoundException;
import org.exchanger.exception.ValidationException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

public class ApiExceptionHandler {
    private static final String CONTENT_TYPE = "application/json";
    private static final String CHARACTER_ENCODING = "UTF-8";
    private static final String INTERNAL_ERROR_MESSAGE = "Internal server error";

    private final ObjectMapper objectMapper;

    public ApiExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void handle(HttpServletResponse response, AppException exception) {
        int status = resolveStatus(exception);
        writeErrorResponse(response, status, exception.getMessage());
    }

    public void handleUnexpected(HttpServletResponse response, Exception unexpected) {
        writeErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, INTERNAL_ERROR_MESSAGE);
    }

    private int resolveStatus(AppException exception) {
        if (exception instanceof ValidationException) {
            return HttpServletResponse.SC_BAD_REQUEST;
        }
        if (exception instanceof EntityNotFoundException) {
            return HttpServletResponse.SC_NOT_FOUND;
        }
        if (exception instanceof ConflictException) {
            return HttpServletResponse.SC_CONFLICT;
        }
        return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    }

    private void writeErrorResponse(HttpServletResponse response, int status, String message) {
        try {
            response.setContentType(CONTENT_TYPE);
            response.setCharacterEncoding(CHARACTER_ENCODING);
            response.setStatus(status);
            objectMapper.writeValue(response.getWriter(), new ErrorResponse(message));
        } catch (IOException ignored) {
        }
    }
}
