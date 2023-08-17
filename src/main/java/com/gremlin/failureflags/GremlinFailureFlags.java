package com.gremlin.failureflags;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gremlin.failureflags.behaviors.DelayedException;
import com.gremlin.failureflags.models.Experiment;
import com.gremlin.failureflags.models.FailureFlag;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GremlinFailureFlags implements FailureFlags {

  private static final Logger LOGGER = LoggerFactory.getLogger(FailureFlags.class);
  private static final String IOEXCEPTION_MESSAGE = "IOException during HTTP call to Gremlin co-process";
  private static final ObjectMapper MAPPER =
      new ObjectMapper();

  public Experiment ifExperimentActive(String name, Map<String, String> labels, Behavior behavior, boolean debug) {
    if (debug) {
      LOGGER.info("ifExperimentActive: name: {}, labels: {}", name, labels);
    }

    Experiment activeExperiment;
    try {
      activeExperiment = fetchExperiment(name, labels, debug);
    } catch (Exception e) {
      if (debug) {
        LOGGER.info("unable to fetch experiment", e);
      }
      return null;
    }

    if (activeExperiment == null) {
      if (debug) {
        LOGGER.info("no experiment for name: {}, labels: {}", name, labels);
      }
      return null;
    }

    if (debug) {
      LOGGER.info("fetched experiment {}", activeExperiment);
    }
    double dice = Math.random();
    if(activeExperiment.getRate() >= 0 &&
        activeExperiment.getRate() <= 1 &&
        dice > activeExperiment.getRate()) {
      if (debug) {
        LOGGER.info("probablistically skipped");
        return null;
      }
    }

    if (behavior == null) {
      new DelayedException().applyBehavior(activeExperiment);
    }
    return activeExperiment;
  }

  public Experiment fetchExperiment(String name, Map<String, String> labels, boolean debug) {
    if (name == null || name.isEmpty()) {
      LOGGER.info("Invalid failure flag name {}", name);
      return null;
    }
    if (debug) {
      LOGGER.info("fetching experiment for: name: {}, labels: {}", name, labels);
    }
    FailureFlag failureFlag = new FailureFlag();
    failureFlag.setName(name);
    failureFlag.setLabels(labels);

    HttpClient client = HttpClient.newBuilder().build();
    try {
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create("http://localhost:5032/experiment"))
          .POST(BodyPublishers.ofString(MAPPER.writeValueAsString(failureFlag)))
          .timeout(Duration.of(150, ChronoUnit.MILLIS))
          .build();

      HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
      int statusCode = response.statusCode();

      if (statusCode == 204) {
        return null;
      } else if (statusCode >= 200 && statusCode < 300) {
        Experiment experiment = MAPPER.readValue(response.body(), Experiment.class);
        return experiment;
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
