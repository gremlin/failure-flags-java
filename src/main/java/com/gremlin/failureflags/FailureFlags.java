package com.gremlin.failureflags;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gremlin.failureflags.exceptions.FailureFlagException;
import com.gremlin.failureflags.exceptions.GremlinCoProcessException;
import com.gremlin.failureflags.models.Experiment;
import com.gremlin.failureflags.models.ExperimentPayload;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FailureFlags {
  private static final Logger LOGGER = LoggerFactory.getLogger(FailureFlags.class);
  private static final String IOEXCEPTION_MESSAGE = "IOException during HTTP call to Gremlin co-process";
  private static final ObjectMapper MAPPER =
      new ObjectMapper();

  public boolean ifExperimentActive(String name, Map<String, String> labels, boolean debug) {
    if (debug) {
      LOGGER.info("ifExperimentActive: name: {}, labels: {}", name, labels);
    }

    Experiment activeExperiment = null;
    try {
      activeExperiment = fetchExperiment(name, labels, debug);
    } catch (GremlinCoProcessException e) {
      if (debug) {
        LOGGER.info("unable to fetch experiment", e);
      }
      return false;
    }

    if (activeExperiment == null) {
      if (debug) {
        LOGGER.info("no experiment for name: {}, labels: {}", name, labels);
      }
      return false;
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
        return false;
      }
    }

    new Faults().delayedException(activeExperiment);
    return true;
  }

  public Experiment fetchExperiment(String name, Map<String, String> labels, boolean debug) {
    if (name == null || name.isEmpty()) {
      throw new FailureFlagException("Invalid failure flag name");
    }
    if (debug) {
      LOGGER.info("fetching experiment for: name: {}, labels: {}", name, labels);
    }

    final RequestConfig requestConfig =
        RequestConfig.custom()
            .setConnectionRequestTimeout(1000)
            .build();

    final RequestBuilder requestBuilder =
        RequestBuilder.post("http://localhost:5032/experiment");

    requestBuilder.setHeader("Content-Type", "application/json");
    requestBuilder.setConfig(requestConfig);
    ExperimentPayload experimentPayload = new ExperimentPayload();
    experimentPayload.setName(name);
    experimentPayload.setLabels(labels);

    try {
      requestBuilder.setEntity(
          new StringEntity(MAPPER.writeValueAsString(experimentPayload)));
    } catch (IOException e) {
      LOGGER.error("Could not set entity to request; requestBody={}" + experimentPayload);
      throw new GremlinCoProcessException(
          String.format("Could not set entity to request; requestBody=%s" + experimentPayload));
    }
    Function<InputStream, Experiment> bodyMappingFunction =
        inputStream -> mapInputStream(Experiment.class, inputStream);
    Optional<Experiment> experiment = execute(
        requestBuilder, bodyMappingFunction);
    if (experiment.isPresent()) {
      return experiment.get();
    }
    return null;
  }

  private <T> T mapInputStream(Class<T> responseClass, InputStream inputStream) {
    try {
      return MAPPER.readValue(inputStream, responseClass);
    } catch (IOException exception) {
      LOGGER.error("Error during reading input stream", exception);
      return null;
    }
  }

  private Optional<Experiment> execute(
      RequestBuilder requestBuilder,
      Function<InputStream, Experiment> bodyDeserializer) {

    HttpClientBuilder builder = HttpClientBuilder.create();
    builder.setDefaultRequestConfig(requestBuilder.getConfig());
    final CloseableHttpClient httpClient = builder.build();

      try (CloseableHttpResponse httpResponse =
          httpClient.execute(requestBuilder.build())) {
        int statusCode = httpResponse.getStatusLine().getStatusCode();

        if (statusCode == 204) {
          return Optional.empty();
        } else if (statusCode >= 200 && statusCode < 300) {
          final Experiment entity = bodyDeserializer.apply(httpResponse.getEntity().getContent());
          return Optional.ofNullable(entity);
        } else {
          throw new GremlinCoProcessException(String.format("Status code %d received from Gremlin coprocess", statusCode));

        }
      } catch (IOException httpException) {
        LOGGER.error(IOEXCEPTION_MESSAGE, httpException);
      }
      return Optional.empty();

  }


}
