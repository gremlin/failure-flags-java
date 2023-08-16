package com.gremlin.failureflags;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.gremlin.failureflags.exceptions.GremlinCoProcessException;
import com.gremlin.failureflags.models.Experiment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;


public class RunFailureFlags {
    private static final Logger LOGGER = LoggerFactory.getLogger(RunFailureFlags.class);

    private static final Fetch fetch = new Fetch();
    public static Experiment ifExperimentActive(String name, Map<String, Object> labels, boolean debug) throws IOException {

        if (debug) {
            LOGGER.info("ifExperimentActive: name: {}, labels: {}", name, labels);
        }

        Experiment experiment = null;
        try {
            experiment = fetch.fetchExperiment(name, labels, debug);
        } catch (GremlinCoProcessException e) {
            if (debug) {
                LOGGER.info("Unable to fetch experiment", e);
            }
            return null;
        }

        if (experiment == null) {
            if (debug) {
                LOGGER.info("no experiment for name: {}, labels: {}", name, labels);
            }
            return null;
        }

        if (debug) {
            LOGGER.info("fetched experiment {}", experiment);
        }

        double dice = Math.random();

        if (experiment.getRate() >= 0 && experiment.getRate() <= 1 && dice > experiment.getRate()) {
            if (debug) {
                LOGGER.info("probabilistically skipped ");
                return null;
            }
        }

        Fault.delayedException(experiment);

        return experiment;
    }


}
