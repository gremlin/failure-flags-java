package com.gremlin.failureflags;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gremlin.failureflags.behaviors.DelayedException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GremlinFailureFlags is a full implementation of FailureFlags that integrates with the Gremlin sidecars and API.
 * */
public class GremlinFailureFlags implements FailureFlags {

  private static final String VERSION = FailureFlags.class.getPackage().getImplementationVersion();
  private static final Logger LOGGER = LoggerFactory.getLogger(FailureFlags.class);
  private static final String IOEXCEPTION_MESSAGE = "IOException during HTTP call to Gremlin co-process";
  private static final ObjectMapper MAPPER = new ObjectMapper();

  /**
   * FAILURE_FLAGS_ENABLED contains the name of the environment variable to use to control the enabled status of this SDK.
   * */
  public static final String FAILURE_FLAGS_ENABLED = "FAILURE_FLAGS_ENABLED";

  private final Behavior defaultBehavior;
  /**
   * enabled overrides any environment configuration to enable or disable this SDK. Setting to true will enable this SDK.
   * */
  protected boolean enabled; // for testing purposes

  /**
   * Construct a new FailureFlags instance with the default configuration.
   * */
  public GremlinFailureFlags() {
    defaultBehavior = new DelayedException();
  }

  /**
   * Construct a new FailureFlags instance with a different default behavior chain.
   * @param defaultBehavior the default behavior to use in all calls to <code>invoke</code> unless overridden by
   *                        parameter on that specific invocation.
   * */
  public GremlinFailureFlags(Behavior defaultBehavior) {
    if (defaultBehavior == null) {
      defaultBehavior = new DelayedException();
    }
    this.defaultBehavior = defaultBehavior;
  }

  /**
   * getDefaultBehavior returns the current Behavior to be used in default invocations.
   * @return the default behavior
   * */
  public Behavior getDefaultBehavior() {
    return this.defaultBehavior;
  }

  /**
   * {@inheritDoc}
   * */
  @Override
  public Experiment[] invoke(FailureFlag flag) {
    return invoke(flag, null);
  }

  /**
   * {@inheritDoc}
   * */
  @Override
  public Experiment[] invoke(FailureFlag flag, Behavior behavior) {
    if (!System.getenv().containsKey(FAILURE_FLAGS_ENABLED) && !this.enabled) {
      return null;
    }
    if (flag == null) {
      return null;
    }
    if (flag.getDebug()) {
      LOGGER.info("ifExperimentActive: name: {}, labels: {}", flag.getName(), flag.getLabels());
    }

    Experiment[] activeExperiments;
    try {
      activeExperiments = fetch(flag);
    } catch (Exception e) {
      if (flag.getDebug()) {
        LOGGER.info("unable to fetch experiments", e);
      }
      return null;
    }

    if (activeExperiments == null) {
      if (flag.getDebug()) {
        LOGGER.info("no experiment for name: {}, labels: {}", flag.getName(), flag.getLabels());
      }
      return null;
    }

    if (flag.getDebug()) {
      LOGGER.info("{} fetched experiments", activeExperiments.length);
    }
    double dice = Math.random();
    List<Experiment> filteredExperiments = new ArrayList<>(activeExperiments.length);
    for (Experiment e: activeExperiments) {
      if(e.getRate() > 0 && e.getRate() <= 1 && dice < e.getRate()) {
        filteredExperiments.add(e);
      }
    }
    Experiment[] experiments = new Experiment[filteredExperiments.size()];
    filteredExperiments.toArray(experiments);

    if (experiments.length <= 0) {
      return null;
    }

    if (behavior == null) {
      this.defaultBehavior.applyBehavior(experiments);
    } else {
      behavior.applyBehavior(experiments);
    }
    return activeExperiments;
  }

  /**
   * {@inheritDoc}
   * */
  @Override
  public Experiment[] fetch(FailureFlag flag) {
    if (!System.getenv().containsKey(FAILURE_FLAGS_ENABLED) && !this.enabled) {
      return null;
    }
    if (flag == null) {
      return null;
    }
    if (flag.getName() == null || flag.getName().isEmpty()) {
      LOGGER.info("Invalid failure flag name {}", flag.getName());
      return null;
    }

    Map<String,String> augmentedLabels = new HashMap<>(flag.getLabels());
    augmentedLabels.put("failure-flags-sdk-version", "java-v"+VERSION);

    if (flag.getDebug()) {
      LOGGER.info("fetching experiments for: name: {}, labels: {}", flag.getName(), augmentedLabels);
    }
    flag.setLabels(augmentedLabels);

    try {
      HttpURLConnection con = (HttpURLConnection) URI.create("http://localhost:5032/experiment").toURL().openConnection();
      con.setRequestMethod("POST");
      con.setRequestProperty("Content-Type", "application/json");
      con.setDoOutput(true);
      String jsonInputString = MAPPER.writeValueAsString(flag);
      try(OutputStream os = con.getOutputStream()) {
        byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
        os.write(input, 0, input.length);
      }

      int statusCode = con.getResponseCode();
      if (statusCode == 204) {
        return null;
      } else if (statusCode >= 200 && statusCode < 300) {
        Experiment[] experiments = null;
        String output;
        StringBuilder responseBody = new StringBuilder();
        try (BufferedReader in = new BufferedReader(
            new InputStreamReader(
                con.getInputStream()))) {

          while ((output = in.readLine()) != null) {
            responseBody.append(output);
          }
        }

        try {
          experiments = MAPPER.readValue(responseBody.toString(), Experiment[].class);
        } catch (JsonProcessingException e) {
          try {
            experiments = new Experiment[]{MAPPER.readValue(responseBody.toString(), Experiment.class)};
          } catch (JsonProcessingException innerE) {
            // it actually broke
          }
        }
        return experiments;
      }
      return null;
    } catch(JsonProcessingException e) {
      LOGGER.error("Unable to serialize or deserialize", e);
    } catch(IOException e) {
      LOGGER.error(IOEXCEPTION_MESSAGE, e);
    } catch(RuntimeException e) {
      LOGGER.error("Something went wrong when sending request", e);
    }
    return null;
  }
}
