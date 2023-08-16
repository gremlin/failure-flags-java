package com.gremlin.failureflags.interfaces;

import com.gremlin.failureflags.Fault;
import com.gremlin.failureflags.Fault.DataObject;
import com.gremlin.failureflags.models.Experiment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public interface Behavior {
    Logger LOGGER = LoggerFactory.getLogger(Fault.class);
    static void timeout(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    static void latency(Experiment experiment){
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

    static void exception(Experiment experiment) {
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
    static Object data(Experiment experiment, Object prototype) {
        if (experiment.getEffect() == null || !(experiment.getEffect().get("data") instanceof DataObject data))
            return prototype;

        Object toUse = prototype != null ? prototype : new Object();

        Object res = new Object();
        //copy data
        //res.copyProperties(toUse);
        //res.copyProperties(data);
        return res;
    }

   static void delayedException(Experiment e) {
        latency(e);
        exception(e);
    }

    static void delayedDataOrException(Experiment e, Object dataPrototype) {
        latency(e);
        exception(e);
        data(e, dataPrototype);
    }
}
