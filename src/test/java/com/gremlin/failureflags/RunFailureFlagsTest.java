package com.gremlin.failureflags;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.gremlin.failureflags.models.Experiment;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RunFailureFlagsTest {
    private static WireMockServer wireMockServer;
    private static final ObjectMapper MAPPER =
            new ObjectMapper();

    public void startMockServer() {
        WireMockConfiguration config = WireMockConfiguration.options().port(5032);
        wireMockServer = new WireMockServer(config);
        wireMockServer.start();
        configureFor("localhost", 5032);
    }

    @Test
    public void ifExperiment_doesNothing_WhenNoExperimentReturned() throws IOException {
        startMockServer();
        stubFor(post(urlEqualTo("/experiment"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")));
        Map<String, Object> labels = new HashMap<>();
        labels.put("key", "value");
        long start = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        RunFailureFlags.ifExperimentActive("test-1", labels, null,null,true);
        long end = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        assertTrue((end-start) < 150);
        wireMockServer.stop();
    }

    @Test
    public void ifExperiment_introducesLatency_whenExperimentReturnedAndLatencyInEffect() throws IOException {
        startMockServer();
        Map<String, Object> effect = new HashMap<>();
        effect.put("latency", 500);
        Experiment experiment = new Experiment();
        experiment.setEffect(effect);
        experiment.setRate(1.0f);

        try {
            stubFor(post(urlEqualTo("/experiment"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody(MAPPER.writeValueAsString(experiment))));
        } catch (JsonProcessingException ignored) {

        }
        Map<String, Object> labels = new HashMap<>();
        labels.put("key", "value");
        long start = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        RunFailureFlags.ifExperimentActive("test-1", labels, null, null, true);
        long end = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        assertTrue((end-start) < 600);
        wireMockServer.stop();
    }

    @Test
    public void ifExperiment_introducesLatency_whenExperimentReturnedAndLatencyInEffectInObject() throws IOException {
        startMockServer();
        Map<String, Object> effect = new HashMap<>();
        Map<String, Object> latencyEffect = new HashMap<>();
        latencyEffect.put("ms", 500);
        latencyEffect.put("jitter", 100);
        effect.put("latency", latencyEffect);
        Experiment experiment = new Experiment();
        experiment.setEffect(effect);
        experiment.setRate(1.0f);

        try {
            stubFor(post(urlEqualTo("/experiment"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody(MAPPER.writeValueAsString(experiment))));
        } catch (JsonProcessingException ignored) {

        }
        Map<String, Object> labels = new HashMap<>();
        labels.put("key", "value");
        long start = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        RunFailureFlags.ifExperimentActive("test-1", labels, null, null, true);
        long end = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        assertTrue((end-start) > 500 && (end-start) < 700);
        wireMockServer.stop();
    }


    @Test
    public void ifExperiment_introducesLatency_whenExperimentReturnedAndLatencyAndExceptionInEffectInObject() {
        startMockServer();
        Map<String, Object> effect = new HashMap<>();
        Map<String, Object> latencyEffect = new HashMap<>();
        latencyEffect.put("ms", 500);
        latencyEffect.put("jitter", 100);
        effect.put("latency", latencyEffect);
        effect.put("exception", "failure from test");
        Experiment experiment = new Experiment();
        experiment.setEffect(effect);
        experiment.setRate(1.0f);

        try {
            stubFor(post(urlEqualTo("/experiment"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody(MAPPER.writeValueAsString(experiment))));
        } catch (JsonProcessingException ignored) {

        }
        Map<String, Object> labels = new HashMap<>();
        labels.put("key", "value");
        long start = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        Exception exception = assertThrows(RuntimeException.class, () -> {
            RunFailureFlags.ifExperimentActive("test-1", labels, null, null, true);
        });
        long end = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        String actualMessage = exception.getMessage();
        String expectedMessage = "Exception injected by failure flag: failure from test";
        assertTrue(actualMessage.contains(expectedMessage));
        assertTrue((end-start) > 500 && (end-start) < 700);
        wireMockServer.stop();
    }


}