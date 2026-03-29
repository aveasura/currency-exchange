package org.exchanger.servlet.parser;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public class CodeParser {

    private static final String REGEX = "[^a-zA-Z]";
    private static final String EMPTY = "";

    public String getCleanPath(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        return normalize(pathInfo);
    }

    public String extractCode(String path, int beginIndex, int endIndex) {
        return path.substring(beginIndex, endIndex);
    }

    public String extractRate(HttpServletRequest request) throws IOException {
        String body = request.getReader().readLine();
        if (body == null || !body.startsWith("rate=")) {
            return null;
        }
        return java.net.URLDecoder.decode(
                body.substring("rate=".length()),
                java.nio.charset.StandardCharsets.UTF_8
        );
    }

    private String normalize(String string) {
        return string.trim().replaceAll(REGEX, EMPTY).toUpperCase();
    }
}