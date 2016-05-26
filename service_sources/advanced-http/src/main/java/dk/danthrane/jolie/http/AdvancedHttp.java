package dk.danthrane.jolie.http;

import jolie.runtime.Value;
import jolie.runtime.ValueVector;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdvancedHttp {

    public Value execute(Value request) throws IOException {
        Value result = Value.create();
        // Parameters
        String urlString = request.getFirstChild("url").strValue();
        String method = getChildOrDefault(request, "method", "GET");
        Boolean followRedirects = getChildOrDefault(request, "followRedirects", true);
        String body = getChildOrDefault(request, "requestBody", null);
        Value authorization = request.hasChildren("auth") ? request.getFirstChild("auth") : null;
        Map<String, String> headers = new HashMap<>();

        ValueVector requestHeaders = request.getChildren("headers");
        for (int i = 0; i < requestHeaders.size(); i++) {
            Value value = requestHeaders.get(i);
            headers.put(value.getFirstChild("field").strValue(), value.getFirstChild("value").strValue());
        }

        if (authorization != null) {
            String username = authorization.getFirstChild("username").strValue();
            String password = authorization.getFirstChild("password").strValue();
            String combined = username + ":" + password;
            String encoded = Base64.getEncoder().encodeToString(combined.getBytes(StandardCharsets.UTF_8));
            headers.put("Authorization", "Basic " + encoded);
        }

        // Initialize connection
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setInstanceFollowRedirects(followRedirects);
        connection.setRequestMethod(method);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            connection.setRequestProperty(entry.getKey(), entry.getValue());
        }

        // Write request body
        if (body != null) {
            connection.setDoOutput(true);
            OutputStream out = connection.getOutputStream();
            out.write(body.getBytes("UTF-8"));
            out.close();
        }

        // Retrieve the response stream
        InputStream responseStream = (connection.getResponseCode() >= 200 && connection.getResponseCode() <= 299) ?
                connection.getInputStream() : connection.getErrorStream();

        // Parse the response body
        String responseBody = new BufferedReader(new InputStreamReader(responseStream))
                .lines()
                .collect(Collectors.joining("\n"));

        // Convert result to Jolie values
        result.setFirstChild("code", connection.getResponseCode());
        result.setFirstChild("body", responseBody);

        ValueVector jolieHeaders = ValueVector.create();
        for (Map.Entry<String, List<String>> entry : connection.getHeaderFields().entrySet()) {
            if (entry.getKey() != null) {
                Value currentHeader = Value.create();
                ValueVector values = ValueVector.create();
                currentHeader.setFirstChild("field", entry.getKey());
                for (String value : entry.getValue()) {
                    values.add(Value.create(value));
                }
                currentHeader.children().put("value", values);
                jolieHeaders.add(currentHeader);
            }
        }
        result.children().put("headers", jolieHeaders);
        return result;
    }

    private static <E> E getChild(Value request, String name) {
        if (request.hasChildren(name)) {
            //noinspection unchecked
            return (E) request.getFirstChild(name).valueObject();
        } else {
            throw new IllegalArgumentException("Child of name '" + name + "' is of invalid type! But instead is " +
                    request.valueObject().getClass());
        }
    }

    private static <E> E getChildOrDefault(Value request, String name, E defaultValue) {
        if (request.hasChildren(name)) {
            //noinspection unchecked
            return (E) request.getFirstChild(name).valueObject();
        } else {
            return defaultValue;
        }
    }

}
