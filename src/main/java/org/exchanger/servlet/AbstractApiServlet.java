package org.exchanger.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import org.exchanger.exception.RequestProcessingException;
import org.exchanger.exception.ResponseProcessingException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

public abstract class AbstractApiServlet extends HttpServlet {

    private static final String CONTENT_TYPE = "application/json";
    private static final String CHARACTER_ENCODING = "UTF-8";

    protected ObjectMapper objectMapper;

    @Override
    public void init() {
        this.objectMapper = (ObjectMapper) getServletContext().getAttribute("objectMapper");
    }

    protected <T> T getService(String attribute, Class<T> serviceClass) {
        return serviceClass.cast(getServletContext().getAttribute(attribute));
    }

    protected void sendResponse(HttpServletResponse response, Object body, int status) {
        try {
            configureResponse(response, status);

            objectMapper.writeValue(response.getWriter(), body);
        } catch (IOException e) {
            throw new ResponseProcessingException("Failed to write response body", e);
        }
    }

    private void configureResponse(HttpServletResponse response, int status) {
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding(CHARACTER_ENCODING);
        response.setStatus(status);
    }
}
