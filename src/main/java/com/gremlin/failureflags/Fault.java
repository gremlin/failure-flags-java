package com.gremlin.failureflags;


import com.gremlin.failureflags.models.PrototypeObject;
import com.gremlin.failureflags.models.Experiment;
import com.gremlin.failureflags.models.LatencyObject;

import java.util.Map;
import java.util.Random;

public class Fault {

    private static void timeout(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void latency(Experiment experiment) {
        Map<String, Object> effectObject = experiment.getEffect();
        Map<String,Object> latency = (Map<String, Object>) effectObject.get("latency");
        if (latency == null)
            return;
        if (latency.get("latency") instanceof Integer) {
            timeout((Integer) latency.get("latency"));
        } else if (latency.get("latency") instanceof String) {
            timeout(Integer.parseInt(latency.get("latency").toString()));
        } else if (latency.get("latency") instanceof LatencyObject latencyObject) {
            int ms = latencyObject.getMs() != null ? latencyObject.getMs() : 0;
            int jitter = latencyObject.getJitter() != null ?
                     latencyObject.getJitter() * new Random().nextInt() : 0;
            timeout(ms + jitter);
        }
    }

    public static void exception(Experiment experiment) {
        Map<String, Object> effectObject = experiment.getEffect();
        Map<String,Object> exception = (Map<String, Object>) effectObject.get("exception");
        if (exception == null)
            return;
        if (exception.get("exception") instanceof String) {
            throw new RuntimeException(exception.get("exception").toString());
        } else if (exception instanceof ExceptionObject exceptionObject) {
            throw new RuntimeException("Exception injected by Failure Flags", exceptionObject);
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