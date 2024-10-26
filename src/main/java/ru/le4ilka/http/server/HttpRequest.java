package ru.le4ilka.http.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private String rawRequest;
    private HttpMethod method;
    private String uri;
    private Map<String, String> parameters;
    private String body;
    private Exception exception;
    private static final Logger LOGGER = LogManager.getLogger(HttpRequest.class);


    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public String getUri() {
        return uri;
    }

    public String getRoutingKey() {
        return method + " " + uri;
    }

    public String getBody() {
        return body;
    }

    public HttpRequest(String rawRequest) {
        this.rawRequest = rawRequest;
        this.parse();
        this.parseHeaders();
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    public boolean containsParameter(String key) {
        return parameters.containsKey(key);
    }

    private void parse() {
        int startIndex = rawRequest.indexOf(' ');
        int endIndex = rawRequest.indexOf(' ', startIndex + 1);
        uri = rawRequest.substring(startIndex + 1, endIndex);
        method = HttpMethod.valueOf(rawRequest.substring(0, startIndex));
        parameters = new HashMap<>();
        if (uri.contains("?")) {
            String[] elements = uri.split("[?]");
            uri = elements[0];
            String[] keysValues = elements[1].split("[&]");

            for (String keysValue : keysValues) {
                String[] keyValue = keysValue.split("=");
                if (keyValue.length < 2) {
                    parameters.put(keyValue[0], null);
                    continue;
                }
                parameters.put(keyValue[0], keyValue[1]);
            }
        }
        if (method == HttpMethod.POST) {
            this.body = rawRequest.substring(rawRequest.indexOf("\r\n\r\n") + 4);
        }
    }

    private void parseHeaders() {
        int startIndex = rawRequest.indexOf("\r\n", rawRequest.indexOf(' ') + 1);
        int endIndex = rawRequest.indexOf("\r\n\r\n") - 4;
        String rawHeaders = rawRequest.substring(startIndex, endIndex);

        Map<String, String> headersMap = new HashMap<>();
        String[] splitRawHeaders = rawHeaders.split("\r\n");
        for (int i = 1; i < splitRawHeaders.length; i++) {
            String key = splitRawHeaders[i].split(": ", 2)[0];
            String value = "";
            if (splitRawHeaders[i].split(": ").length > 1) {
                value = splitRawHeaders[i].split(": ", 2)[1];
            }
            headersMap.put(key, value);
        }
    }

    public void info() {
        LOGGER.debug("Первоначальный запрос: {}", rawRequest);
        LOGGER.info("Method: {}", method);
        LOGGER.info("URI: {}", uri);
        LOGGER.info("Parameters: {}", parameters);
        LOGGER.info("Body: {}", body);
    }
}
