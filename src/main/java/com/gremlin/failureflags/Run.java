package com.gremlin.failureflags;


import com.gremlin.failureflags.models.Experiment;
import com.gremlin.failureflags.models.PrototypeObject;

import java.io.IOException;
import java.util.Map;

public class Run {
    static Experiment experiment;
    static PrototypeObject defaultBehavior = new PrototypeObject();
    public static void ifExperimentActive(String name, Map<String, Object> labels, boolean debug) throws IOException {

        if(debug){
            System.out.println("ifExperimentActive " + name + " " + labels);
        }

        try {
            experiment = Fetch.fetchExperiment(name, labels, debug);
        } catch (Exception ignore) {
            if (debug) {
                System.out.println("Unable to fetch experiment: " + ignore.getMessage());
            }
        }

        if (experiment == null) {
            if (debug) {
                System.out.println("no experiment for " + name + " " + labels);
            }
        }

        if (debug) {
            System.out.println("fetched experiment " + experiment);
        }
        double dice = Math.random();

        if (experiment.getRate() != null && experiment.getRate() >= 0 && experiment.getRate() <= 1 && dice > experiment.getRate()) {
            if (debug) {
                System.out.println("probablistically skipped " + new RuntimeException());
            }

        }
        try {
            if (defaultBehavior != null) {
                Fault.delayedDataOrException(experiment, defaultBehavior);
            } else {
                Fault.delayedException(experiment);
            }
        } catch (Exception behaviorError) {
            if (debug) {
                System.out.println("provided behavior error: " + behaviorError);
            }
            throw behaviorError;
        }
    }


}
