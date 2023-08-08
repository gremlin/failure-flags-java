package com.javaSDK;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class fetch {
    private static int port;

    public static String fetchExperiment(String name, String s) throws IOException {
        String url = "http://localhost:8080/experiment";
        String payload = String.format("{\"name\": \"%s\", \"labels\": {}}", name);

        URL apiUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = payload.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int statusCode = connection.getResponseCode();
        if (statusCode >= 200 && statusCode < 300) {
            // If the response is successful, read and return the content
            try (InputStream inputStream = connection.getInputStream()) {
                byte[] responseBytes = inputStream.readAllBytes();
                return new String(responseBytes, StandardCharsets.UTF_8);
            }
        } else {
            // Handle error response
            throw new IOException("HTTP status code: " + statusCode + ", message: " + connection.getResponseMessage());
        }
    }

    public static void setPort(int port) {
        fetch.port = port;
    }
}
