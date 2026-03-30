package org.exchanger.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.exchanger.dto.response.ErrorResponse;
import org.exchanger.exception.AppException;
import org.exchanger.exception.ResponseProcessingException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

public abstract class AbstractApiServlet extends HttpServlet {

    private static final String CONTENT_TYPE = "application/json";
    private static final String CHARACTER_ENCODING = "UTF-8";
    private static final String INTERNAL_ERROR_MESSAGE = "Internal server error";

    protected ObjectMapper objectMapper;

    @Override
    public void init() {
        this.objectMapper = (ObjectMapper) getServletContext().getAttribute("objectMapper");
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            super.service(request, response);
        } catch (AppException e) {
            writeErrorResponse(response, e.getStatus(), e.getMessage());
        } catch (Exception e) {
            log("Unexpected server error", e);
            writeErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, INTERNAL_ERROR_MESSAGE);
        }
    }

    protected <T> T getService(String attribute, Class<T> serviceClass) {
        return serviceClass.cast(getServletContext().getAttribute(attribute));
    }

    protected void sendResponse(HttpServletResponse response, Object body, int status) {
        writeJsonResponse(response, body, status);
    }

    private void writeErrorResponse(HttpServletResponse response, int status, String message) {
        writeJsonResponse(response, new ErrorResponse(message), status);
    }

    private void writeJsonResponse(HttpServletResponse response, Object body, int status) {
        try {
            configureResponse(response, status);
            objectMapper.writeValue(response.getWriter(), body);
        } catch (IOException e) {
            log("Failed to write response body", e);
        }
    }

    private void configureResponse(HttpServletResponse response, int status) {
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding(CHARACTER_ENCODING);
        response.setStatus(status);
    }
}
