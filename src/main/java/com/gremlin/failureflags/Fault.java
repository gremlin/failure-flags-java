package com.gremlin.failureflags;


import com.gremlin.failureflags.models.PrototypeObject;
import com.gremlin.failureflags.models.Experiment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Map;

public class Fault {
    private static final Logger LOGGER = LoggerFactory.getLogger(Fault.class);
    private static void timeout(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void latency(Experiment experiment) {
        if(experiment.getEffect().containsKey("latency")) {
            Object latency = experiment.getEffect().get("latency");
            if(latency instanceof String || latency instanceof Integer){
                try {
                    int latencyToInject = Integer.parseInt(experiment.getEffect().get("latency").toString());
                    timeout(latencyToInject);
                } catch (NumberFormatException nfe) {
                    LOGGER.info("Invalid value for latency passed");
                }

            } else if (latency != null) {
                Map<String, String> latencyMap = (Map<String, String>) latency;
                Object ms = latencyMap.get("ms");
                Object jitter = latencyMap.get("jitter");
                if ((ms instanceof String || ms instanceof Integer) && (jitter instanceof String || jitter instanceof Integer)) {
                    try {
                        int latencyToInject = Integer.parseInt(ms.toString());
                        long jitterMs = Integer.parseInt(jitter.toString());
                        timeout(latencyToInject + (jitterMs == 0 ? 0 : (long) (Math.random() * jitterMs)));
                    } catch (NumberFormatException nfe) {
                        LOGGER.info("Unsupported effect statement");
                    }
                }
            }
        }
    }

    public static void exception(Experiment experiment) {
        if (!experiment.getEffect().containsKey("exception")){
            return;
        }
        Object exception =experiment.getEffect().get("exception");
        if (exception instanceof String) {
            throw new RuntimeException("Exception injected by failure flag: " + exception);
        } else if (exception instanceof Map) {
            Map<String, String> exceptionMap = (Map<String, String>) exception;
            if (exceptionMap.containsKey("message") && exceptionMap.containsKey("name")) {
                throw new RuntimeException("Exception injected by failure flag: message: "+ exceptionMap.get("message")
                        + " name: " +exceptionMap.get("name"));
            }
        }
    }

    //not worried about data related items yet
    public static PrototypeObject data(Experiment experiment, PrototypeObject prototype) {
        if (experiment.getEffect() == null || !(experiment.getEffect().get("data") instanceof DataObject data))
            return prototype;

        PrototypeObject toUse = prototype != null ? prototype : new PrototypeObject();

        PrototypeObject res = new PrototypeObject();
        res.copyProperties(toUse);
        res.copyProperties(data);
        return res;
    }

    public static void delayedException(Experiment e) {
        latency(e);
        exception(e);
    }

    public static void delayedDataOrException(Experiment e, PrototypeObject dataPrototype) {
        latency(e);
        exception(e);
        data(e, dataPrototype);
    }

    public static class ExceptionObject extends Throwable {
    }

    public static class DataObject extends PrototypeObject {
    }

}