package com.gremlin.failureflags;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.gremlin.failureflags.behaviors.DelayedException;
import com.gremlin.failureflags.behaviors.Latency;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import wiremock.com.google.common.collect.ImmutableMap;

public class FailureFlagsTest {

  private static WireMockServer wireMockServer;
  private static final ObjectMapper MAPPER = new ObjectMapper();

  @BeforeAll
  public static void startMockServer() {
    WireMockConfiguration config = WireMockConfiguration.options().port(5032);
    wireMockServer = new WireMockServer(config);
    wireMockServer.start();
    configureFor("localhost", 5032);
  }

  @AfterAll
  public static void stopMockServer() {
    wireMockServer.stop();
  }

  @Test
  public void invoke_doesNothing_WhenNoExperimentReturned() {
      stubFor(post(urlEqualTo("/experiment"))
          .willReturn(aResponse()
              .withStatus(200)
              .withHeader("Content-Type", "application/json")));
    GremlinFailureFlags failureFlags = new GremlinFailureFlags((experiments) -> { fail("behavior must not be called"); });
    failureFlags.enabled = true;
    failureFlags.invoke(new FailureFlag("test-1", new HashMap<>(), false));
  }

  @Test
  public void invoke_doesNothing_WhenNoExperimentReturnedWhenBehaviorPassed() {
    stubFor(post(urlEqualTo("/experiment"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")));
    GremlinFailureFlags failureFlags = new GremlinFailureFlags(
            (experiments) -> { fail("default behavior must not be called"); });
    failureFlags.enabled = true;
    failureFlags.invoke(
            new FailureFlag("test-1", new HashMap<String,String>(){{put("method", "POST");}}, true),
            (experiments) -> { fail("custom behavior must not be called"); });
  }

  @Test
  public void invoke_introducesLatency_whenExperimentReturnedAndLatencyInEffect() {
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
    } catch (JsonProcessingException e) {}
    Map<String, String> labels = new HashMap<>();
    labels.put("key", "value");
    long start = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
    GremlinFailureFlags failureFlags = new GremlinFailureFlags();
    failureFlags.enabled = true;
    failureFlags.invoke(new FailureFlag("test-1", labels, true));
    long end = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
    assertTrue((end-start) < 700);
  }

  @Test
  public void invoke_behaviorCalledWhenExperiment100PercentProbable() {
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
    } catch (JsonProcessingException e) {}

    final boolean[] wasCalled = {false};
    GremlinFailureFlags failureFlags = new GremlinFailureFlags((experiments)-> { wasCalled[0] = true; });
    failureFlags.enabled = true;
    failureFlags.invoke(new FailureFlag("test-1", new HashMap<>(), true));
    assertTrue(wasCalled[0]);
  }

  @Test
  public void invoke_behaviorNotCalledWhenExperimentZeroPercentProbable() {
    Map<String, Object> effect = new HashMap<>();
    effect.put("latency", 500);
    Experiment experiment = new Experiment();
    experiment.setEffect(effect);
    experiment.setRate(0f);

    try {
      stubFor(post(urlEqualTo("/experiment"))
              .willReturn(aResponse()
                      .withStatus(200)
                      .withHeader("Content-Type", "application/json")
                      .withBody(MAPPER.writeValueAsString(new Experiment[]{experiment}))));
    } catch (JsonProcessingException e) {}

    final boolean[] wasCalled = { false };
    GremlinFailureFlags failureFlags = new GremlinFailureFlags((experiments)-> { wasCalled[0] = true; });
    failureFlags.enabled = true;
    failureFlags.invoke(new FailureFlag("test-1", new HashMap<>(), true));
    assertFalse(wasCalled[0]);
  }

  @Test
  public void invoke_behaviorNotCalledWhenDisabled() {
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
                      .withBody(MAPPER.writeValueAsString(new Experiment[]{experiment}))));
    } catch (JsonProcessingException e) {}

    final boolean[] wasCalled = { false };
    GremlinFailureFlags failureFlags = new GremlinFailureFlags((experiments)-> { wasCalled[0] = true; });
    failureFlags.enabled = false;
    failureFlags.invoke(new FailureFlag("test-1", new HashMap<>(), true));
    assertFalse(wasCalled[0]);
  }

  @Test
  public void invoke_introducesLatency_whenExperimentReturnedAndLatencyInEffectAndLatencyBehaviorPassed() {
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
    } catch (JsonProcessingException e) {

    }
    Map<String, String> labels = new HashMap<>();
    labels.put("key", "value");
    long start = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
    GremlinFailureFlags failureFlags = new GremlinFailureFlags();
    failureFlags.enabled = true;
    failureFlags.invoke(new FailureFlag("test-1", labels, true), new Latency());
    long end = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
    assertTrue((end-start) < 700);
  }

  @Test
  public void invoke_introducesTwoLatency_whenTwoExperimentsReturnedAndLatencyInEffectAndLatencyBehaviorPassed() {
    Map<String, Object> effect = new HashMap<>();
    effect.put("latency", 500);
    Experiment exp1 = new Experiment();
    exp1.setEffect(effect);
    exp1.setRate(1.0f);
    Experiment exp2 = new Experiment();
    exp2.setEffect(effect);
    exp2.setRate(1.0f);
    List<Experiment> experiments = Arrays.asList(exp1, exp2);

    try {
      stubFor(post(urlEqualTo("/experiment"))
          .willReturn(aResponse()
              .withStatus(200)
              .withHeader("Content-Type", "application/json")
              .withBody(MAPPER.writeValueAsString(experiments))));
    } catch (JsonProcessingException e) {

    }
    Map<String, String> labels = new HashMap<>();
    labels.put("key", "value");
    long start = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
    GremlinFailureFlags failureFlags = new GremlinFailureFlags();
    failureFlags.enabled = true;
    failureFlags.invoke(new FailureFlag("test-1", labels, true), new Latency());
    long end = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
    assertTrue((end-start) > 900);
  }

  @Test
  public void invoke_introducesLatency_whenExperimentReturnedAndLatencyInEffectInObject() {
    Map<String, Object> effect = new HashMap<>();
    Map<String, Object> latencyEffect = new HashMap<>();
    latencyEffect.put("ms", 500);
    latencyEffect.put("jitter", 100);
    effect.put("latency", latencyEffect);
    Experiment experiment = new Experiment();
    experiment.setEffect(effect);
    experiment.setRate(1.0f);
    Experiment[] experiments = new Experiment[]{experiment};

    try {
      stubFor(post(urlEqualTo("/experiment"))
          .willReturn(aResponse()
              .withStatus(200)
              .withHeader("Content-Type", "application/json")
              .withBody(MAPPER.writeValueAsString(experiment))));
    } catch (JsonProcessingException e) {}
    Map<String, String> labels = new HashMap<>();
    labels.put("key", "value");
    long start = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
    GremlinFailureFlags failureFlags = new GremlinFailureFlags();
    failureFlags.enabled = true;
    failureFlags.invoke(new FailureFlag("test-1", labels, true));
    long end = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
    assertTrue((end-start) > 500 && (end-start) < 800);
  }

  @Test
  public void invoke_introducesLatency_whenExperimentReturnedAndLatencyAndExceptionInEffectInObject() {
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
    } catch (JsonProcessingException e) {

    }
    Map<String, String> labels = new HashMap<>();
    labels.put("key", "value");
    long start = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
    GremlinFailureFlags failureFlags = new GremlinFailureFlags();
    failureFlags.enabled = true;
    Exception exception = assertThrows(FailureFlagException.class, () -> {
      failureFlags.invoke(new FailureFlag("test-1", labels, true));
    });
    long end = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
    String actualMessage = exception.getMessage();
    String expectedMessage = "Exception injected by failure flag: failure from test";
    assertTrue(actualMessage.contains(expectedMessage));
    assertTrue((end-start) > 500 && (end-start) < 800);
  }

  @Test
  public void invoke_introducesLatency_whenExperimentReturnedAndLatencyAndExceptionInEffectInObjectAndBehaviorPassed() {
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
    } catch (JsonProcessingException e) {}
    Map<String, String> labels = new HashMap<>();
    labels.put("key", "value");
    long start = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
    GremlinFailureFlags failureFlags = new GremlinFailureFlags();
    failureFlags.enabled = true;
    Exception exception = assertThrows(FailureFlagException.class, () -> {
      failureFlags.invoke(new FailureFlag("test-1", labels, true), new DelayedException());
    });
    long end = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
    String actualMessage = exception.getMessage();
    String expectedMessage = "Exception injected by failure flag: failure from test";
    assertTrue(actualMessage.contains(expectedMessage));
    assertTrue((end-start) > 500 && (end-start) < 800);
  }

  @Test
  public void invoke_throwsExceptionWithMessage_whenExperimentReturnedAndExceptionInEffectInObjectAndBehaviorPassed() {
    Map<String, Object> effect = new HashMap<>();
    String exceptionMessage = "exception message";
    effect.put("exception", ImmutableMap.of("message", exceptionMessage));
    Experiment experiment = new Experiment();
    experiment.setEffect(effect);
    experiment.setRate(1.0f);

    try {
      stubFor(post(urlEqualTo("/experiment"))
          .willReturn(aResponse()
              .withStatus(200)
              .withHeader("Content-Type", "application/json")
              .withBody(MAPPER.writeValueAsString(experiment))));
    } catch (JsonProcessingException ignored) {}

    GremlinFailureFlags failureFlags = new GremlinFailureFlags();
    failureFlags.enabled = true;
    Exception exception = assertThrows(FailureFlagException.class,
        () -> failureFlags.invoke(new FailureFlag("test-1",
            ImmutableMap.of("key", "value"),
            true),
        new DelayedException()));
    String expectedMessage = "Exception injected by failure flag: " + exceptionMessage;
    assertEquals(expectedMessage, exception.getMessage());

    // For backwards compat we still support the old form
    effect.put("exception", exceptionMessage);
    Exception secondException = assertThrows(FailureFlagException.class,
        () -> failureFlags.invoke(new FailureFlag("test-1",
                ImmutableMap.of("key", "value"),
                true),
            new DelayedException()));
    assertEquals(expectedMessage, secondException.getMessage());
  }

}
