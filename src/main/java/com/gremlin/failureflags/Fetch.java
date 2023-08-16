package com.gremlin.failureflags;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gremlin.failureflags.models.Experiment;
import com.gremlin.failureflags.models.FailureFlag;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class Fetch {
    private static final Logger LOGGER = LoggerFactory.getLogger(Fetch.class);
    private static final String IOEXCEPTION_MESSAGE = "IOException during HTTP call to Gremlin co-process";
    private static final ObjectMapper MAPPER =
            new ObjectMapper();

    public Experiment fetchExperiment(String name, Map<String, Object> labels, boolean debug) throws IOException {

        if (name == null || name.isEmpty()) {
            LOGGER.info("Invalid failure flag name");
            return null;
        }
        if (debug) {
            LOGGER.info("ifExperimentActive: name: {}, labels: {}", name, labels);
        }

        final RequestConfig requestConfig =
                RequestConfig.custom()
                        .setConnectionRequestTimeout(1000)
                        .build();

        final RequestBuilder requestBuilder =
                RequestBuilder.post("http://localhost:5032/experiment");

        requestBuilder.setHeader("Content-Type", "application/json");
        requestBuilder.setConfig(requestConfig);
        FailureFlag failureFlag = new FailureFlag();
        failureFlag.setName(name);
        failureFlag.setLabels(labels);

        try {
            requestBuilder.setEntity(
                    new StringEntity(MAPPER.writeValueAsString(failureFlag)));
        } catch (IOException e) {
            LOGGER.info("Could not set entity to request; requestBody={}", failureFlag);
            return null;
        }
        Function<InputStream, Experiment> bodyMappingFunction =
                inputStream -> mapInputStream(Experiment.class, inputStream);
        Optional<Experiment> experiment = execute(
                requestBuilder, bodyMappingFunction);
        return experiment.orElse(null);
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
                LOGGER.info("Status code {} received from Gremlin coprocess", statusCode);
                return Optional.empty();
            }
        } catch (IOException httpException) {
            LOGGER.error(IOEXCEPTION_MESSAGE, httpException);
        }
        return Optional.empty();

    }
}