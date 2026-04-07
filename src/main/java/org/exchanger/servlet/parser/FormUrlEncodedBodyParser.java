package org.exchanger.servlet.parser;

import jakarta.servlet.http.HttpServletRequest;
import org.exchanger.exception.RequestReadException;
import org.exchanger.exception.ValidationException;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class FormUrlEncodedBodyParser {

    public FormUrlEncodedBody parse(HttpServletRequest request) {
        String body = readBody(request);

        if (body.isBlank()) {
            throw new ValidationException("Request body is empty");
        }

        Map<String, String> params = new HashMap<>();

        for (String pair : body.split("&")) {
            String[] parts = pair.split("=", 2);

            String key = decode(parts[0]);
            String value = parts.length > 1 ? decode(parts[1]) : "";

            params.put(key, value);
        }

        return new FormUrlEncodedBody(params);
    }

    private String readBody(HttpServletRequest request) {
        try {
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                body.append(line);
            }
            return body.toString();
        } catch (IOException e) {
            throw new RequestReadException("Failed to read request body", e);
        }
    }

    private String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}