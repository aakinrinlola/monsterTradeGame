package at.fhtw.httpserver.utils;

import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Locale;

public class RequestBuilder {
    public Request buildRequest(BufferedReader bufferedReader) throws IOException {
        Request request = new Request();
        String line = bufferedReader.readLine();

        if (line != null) {
            String[] splitFirstLine = line.split(" ");

            request.setMethod(getMethod(splitFirstLine[0]));
            setPathname(request, splitFirstLine[1]);

            // Header einlesen
            while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
                parseHeader(request, line);
            }

            // Body einlesen, falls vorhanden
            if (request.getHeader("Content-Length") != null) {
                int contentLength = Integer.parseInt(request.getHeader("Content-Length"));
                if (contentLength > 0) {
                    char[] charBuffer = new char[contentLength];
                    bufferedReader.read(charBuffer, 0, contentLength);
                    request.setBody(new String(charBuffer));
                }
            }
        }

        return request;
    }

    private void parseHeader(Request request, String line) {
        String[] parts = line.split(": ", 2);
        if (parts.length == 2) {
            request.addHeader(parts[0].trim(), parts[1].trim());
        }
    }

    private Method getMethod(String methodString) {
        return Method.valueOf(methodString.toUpperCase(Locale.ROOT));
    }

    private void setPathname(Request request, String path) {
        boolean hasParams = path.contains("?");

        if (hasParams) {
            String[] pathParts = path.split("\\?");
            request.setPathname(pathParts[0]);
            request.setParams(pathParts[1]);
        } else {
            request.setPathname(path);
            request.setParams(null);
        }
    }
}