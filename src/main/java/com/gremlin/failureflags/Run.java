package com.gremlin.failureflags;


import com.gremlin.failureflags.models.Experiment;
import com.gremlin.failureflags.models.PrototypeObject;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

public class Run {
    static Experiment experiment;
    static PrototypeObject defaultBehavior = new PrototypeObject();
    public static boolean ifExperimentActive(String name, Map<String, Object> labels, boolean debug) throws IOException {

        if(debug){
            Logger.getLogger("ifExperimentActive " + name + " " + labels);
        }

        try {
            experiment = Fetch.fetchExperiment(name, labels, debug);
        } catch (Exception ignore) {
            if (debug) {
                Logger.getLogger("Unable to fetch experiment: " + ignore.getMessage());
            }
        }

        if (experiment == null) {
            if (debug) {
                Logger.getLogger("no experiment for " + name + " " + labels);
            }
        }

        if (debug) {
            Logger.getLogger("fetched experiment " + experiment);
        }
        double dice = Math.random();

        if (experiment.getRate() != null && experiment.getRate() >= 0 && experiment.getRate() <= 1 && dice > experiment.getRate()) {
            if (debug) {
                Logger.getLogger("probabilistically skipped " + new RuntimeException());
            }

        }
        try {
            if (defaultBehavior != null) {
                Fault.delayedDataOrException(experiment, defaultBehavior);
            } else {
                Fault.delayedException(experiment);
                debug = true;
            }
        } catch (Exception behaviorError) {
            if (debug) {
                Logger.getLogger("provided behavior error: " + behaviorError);
            }
            throw behaviorError;
        }
    }


}
