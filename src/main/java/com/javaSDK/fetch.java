package com.javaSDK;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class fetch {
    public static void main(String[] args) {
        try {
            fetchExperiment("example", new HashMap<>(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void fetchExperiment(String name, Map<String, Object> labels, boolean debug) throws IOException {
        if (debug) System.out.println("fetch experiment for " + name + " " + labels);
        if (name == null) {
            throw new IllegalArgumentException("invalid failure-flag name");
        }
        String postData = "{\"name\":\"" + name + "\",\"labels\":" + labels.toString() + "}";
        byte[] postDataBytes = postData.getBytes(StandardCharsets.UTF_8);

        URL url = new URL("http://localhost:5032/experiment");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(1000);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(postDataBytes);
        }

        int responseCode = conn.getResponseCode();
        if (responseCode < 200 || responseCode > 299) {
            throw new IOException("HTTP status code: " + responseCode + ", message: " + conn.getResponseMessage());
        } else {
            System.out.println("HTTP status code: " + responseCode + ", message: " + conn.getResponseMessage());
        }
    }
}