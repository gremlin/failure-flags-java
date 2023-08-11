package com.javaSDK;

import com.javaSDK.fault.EffectObject;
import com.javaSDK.fault.Experiment;
import com.javaSDK.fault.PrototypeObject;

import java.io.IOException;
import java.util.Map;

public class run {
    static Experiment experiment = new Experiment();
    static PrototypeObject defaultBehavior = new PrototypeObject();
    public static void ifExperimentActive(String name, Map<String, Object> labels, boolean debug) throws IOException {


        if(debug){
            System.out.println("ifExperimentActive " + name + " " + labels);
        }

        try {
            experiment = fetch.fetchExperiment(name, labels, debug);
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
        //rating coming in from user input
        if (experiment.rate != null &&
                experiment.rate instanceof Double &&
                experiment.rate >= 0 &&
                experiment.rate <= 1 &&
                dice > experiment.rate) {
            if (debug) {
                System.out.println("probablistically skipped " + new RuntimeException());
            }

        }
        try {
            if (defaultBehavior != null) {
                fault.delayedDataOrException(experiment, defaultBehavior);
            } else {
                fault.delayedException(experiment);
            }
        } catch (Exception behaviorError) {
            if (debug) {
                System.out.println("provided behavior error: " + behaviorError);
            }
            throw behaviorError;
        }
    }


}
