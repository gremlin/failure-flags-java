package com.javaSDK;

import com.javaSDK.fault.EffectObject;
import com.javaSDK.fault.Experiment;
import com.javaSDK.fault.PrototypeObject;

import java.io.IOException;
import java.util.Map;

public class run {

    //get experiment from fetch
    //get effectObject from fault
    PrototypeObject defaultBehavior =fault.delayedDataOrException(experiment, effectObject);

    public static boolean ifExperimentActive(String name, Map<String, Object> labels, boolean debug) throws IOException {
        if(debug){
            System.out.println("ifExperimentActive " + name + " " + labels);
        }

        Experiment experiment = null;
        try {
            experiment = fetch.fetchExperiment(name, labels, debug);
        } catch (Exception ignore) {
            if (debug) {
                System.out.println("Unable to fetch experiment: " + ignore.getMessage());
            }
            return resolveOrFalse(debug, dataPrototype);
        }

        if (experiment == null) {
            if (debug) {
                System.out.println("no experiment for " + name + " " + labels);
            }
            return resolveOrFalse(debug, dataPrototype);
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
                System.out.println("probablistically skipped " + behaviorError);
            }
            return resolveOrFalse(debug, dataPrototype);
        }
        try {
            if (dataPrototype != null) {
                return behavior(experiment, dataPrototype);
            } else {
                behavior(experiment);
                return true;
            }
        } catch (Exception behaviorError) {
            if (debug) {
                System.out.println("provided behavior error: " + behaviorError);
            }
            throw behaviorError;
        }
    }

    public static Object resolveOrFalse(boolean debug, Object dataPrototype) {
        Object value = (dataPrototype != null) ? dataPrototype : false;

        if (debug) {
            System.out.println("returning " + value);
        }

        return value;
    }

}
