package com.gremlin.failureflags;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gremlin.failureflags.models.Experiment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Fetch {
    public static void main(String[] args) {
        try {
            fetchExperiment("example", new HashMap<>(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //here im fetching all active experiments from the side cart
    public static Experiment fetchExperiment(String name, Map<String, Object> labels, boolean debug) throws IOException {
        Experiment experiment = new Experiment();

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
        ObjectMapper objectMapper = new ObjectMapper();
        int responseCode = conn.getResponseCode();
        if (responseCode >= 200 && responseCode < 300) {
            try (InputStream inputStream = conn.getInputStream()) {
                // Parse the response content into a Map
                @SuppressWarnings("unchecked")
                Map<String, Object> jsonResponse = objectMapper.readValue(inputStream, Map.class);

                experiment.setGuid((String) jsonResponse.get("failureFlagName"));
                experiment.setGuid((String) jsonResponse.get("guid"));
                experiment.setRate((double) jsonResponse.get("rate"));
                experiment.setSelector((Map<String, List<String>>) jsonResponse.get("selector"));
                experiment.setEffect((Map<String, Object>) jsonResponse.get("effect"));

                System.out.println("JSON Response: " + jsonResponse);
            }
        } else {
            System.out.println("HTTP status code: " + responseCode);
        }

        return experiment;
    }
}