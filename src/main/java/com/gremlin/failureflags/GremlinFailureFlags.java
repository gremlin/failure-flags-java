package com.gremlin.failureflags;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gremlin.failureflags.behaviors.DelayedException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GremlinFailureFlags implements FailureFlags {

  private static final String VERSION = FailureFlags.class.getPackage().getImplementationVersion();
  private static final Logger LOGGER = LoggerFactory.getLogger(FailureFlags.class);
  private static final String IOEXCEPTION_MESSAGE = "IOException during HTTP call to Gremlin co-process";
  private static final ObjectMapper MAPPER = new ObjectMapper();

  public static final String FAILURE_FLAGS_ENABLED = "FAILURE_FLAGS_ENABLED";

  private final Behavior defaultBehavior;
  protected boolean enabled; // for testing purposes

  /**
   * Construct a new FailureFlags instance with the default configuration.
   * */
  public GremlinFailureFlags() {
    defaultBehavior = new DelayedException();
  }

  /**
   * Construct a new FailureFlags instance with a different default behavior chain.
   * */
  public GremlinFailureFlags(Behavior defaultBehavior) {
    if (defaultBehavior == null) {
      defaultBehavior = new DelayedException();
    }
    this.defaultBehavior = defaultBehavior;
  }

  public Behavior getDefaultBehavior() {
    return this.defaultBehavior;
  }

  /**
   * {@inheritDoc}
   * */
  public Experiment[] invoke(FailureFlag flag) {
    return invoke(flag, null);
  }

  /**
   * {@inheritDoc}
   * */
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
  public Experiment[] fetch(FailureFlag flag) {
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

    HttpClient client = HttpClient.newBuilder().build();
    try {
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create("http://localhost:5032/experiment"))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(MAPPER.writeValueAsString(flag)))
          .timeout(Duration.of(150, ChronoUnit.MILLIS))
          .build();

      HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
      int statusCode = response.statusCode();

      if (statusCode == 204) {
        return null;
      } else if (statusCode >= 200 && statusCode < 300) {
        Experiment[] experiments = null;
        try {
          experiments = MAPPER.readValue(response.body(), Experiment[].class);
        } catch (JsonProcessingException e) {
          try {
            experiments = new Experiment[]{MAPPER.readValue(response.body(), Experiment.class)};
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
    } catch(InterruptedException e) {
      LOGGER.error("Something went wrong when sending request", e);
    }
    return null;
  }
}
