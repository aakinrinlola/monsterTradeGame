package at.fhtw.httpserver.server;

import at.fhtw.httpserver.http.Method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {
    private Method method;
    private String urlContent;
    private String pathname;
    private List<String> pathParts;
    private String params;
    private String body;
    private Map<String, String> headers = new HashMap<>();

    public String getServiceRoute() {
        if (this.pathParts == null || this.pathParts.isEmpty()) {
            return null;
        }
        return '/' + this.pathParts.get(0);
    }

    public String getUrlContent() {
        return this.urlContent;
    }

    public void setUrlContent(String urlContent) {
        this.urlContent = urlContent;
        boolean hasParams = urlContent.contains("?");

        if (hasParams) {
            String[] pathParts = urlContent.split("\\?");
            this.setPathname(pathParts[0]);
            this.setParams(pathParts[1]);
        } else {
            this.setPathname(urlContent);
            this.setParams(null);
        }
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getPathname() {
        return pathname;
    }

    public void setPathname(String pathname) {
        this.pathname = pathname;
        String[] stringParts = pathname.split("/");
        this.pathParts = new ArrayList<>();
        for (String part : stringParts) {
            if (part != null && part.length() > 0) {
                this.pathParts.add(part);
            }
        }
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<String> getPathParts() {
        return pathParts;
    }

    public void setPathParts(List<String> pathParts) {
        this.pathParts = pathParts;
    }

    public String getHeader(String headerName) {
        for (String key : headers.keySet()) {
            if (key.equalsIgnoreCase(headerName)) {  // Case-Insensitive Vergleich
                return headers.get(key);
            }
        }
        return null;
    }

    public void parseHeaders(String rawHeaders) {
        System.out.println("Empfangene Header-Rohdaten:\n" + rawHeaders);

        String[] lines = rawHeaders.split("\r\n");
        for (String line : lines) {
            String[] parts = line.split(": ", 2);
            if (parts.length == 2) {
                headers.put(parts[0].trim(), parts[1].trim());
            }
        }

        // Debugging: Zeigt alle geparsten Header
        System.out.println("Geparste Header:");
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }
}