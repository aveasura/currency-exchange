package org.exchanger.servlet.error;

import jakarta.servlet.http.HttpServletResponse;
import org.exchanger.dto.response.ErrorResponse;
import org.exchanger.exception.AppException;
import org.exchanger.exception.ConflictException;
import org.exchanger.exception.EntityNotFoundException;
import org.exchanger.exception.ValidationException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApiExceptionHandler {

    private static final Logger LOGGER = Logger.getLogger(ApiExceptionHandler.class.getName());

    private static final String CONTENT_TYPE = "application/json";
    private static final String CHARACTER_ENCODING = "UTF-8";
    private static final String INTERNAL_ERROR_MESSAGE = "Internal server error";

    private final ObjectMapper objectMapper;

    public ApiExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void handle(HttpServletResponse response, AppException exception) {
        writeIfPossible(response, resolveStatus(exception), exception.getMessage());
    }

    public void handleUnexpected(HttpServletResponse response) {
        writeIfPossible(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, INTERNAL_ERROR_MESSAGE);
    }

    private void writeIfPossible(HttpServletResponse response, int status, String message) {
        if (response.isCommitted()) {
            return;
        }

        response.resetBuffer();
        writeErrorResponse(response, status, message);
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
            configureResponse(response, status);
            objectMapper.writeValue(response.getWriter(), new ErrorResponse(message));
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to write error response", e);
        }
    }

    private void configureResponse(HttpServletResponse response, int status) {
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding(CHARACTER_ENCODING);
        response.setStatus(status);
    }
}
