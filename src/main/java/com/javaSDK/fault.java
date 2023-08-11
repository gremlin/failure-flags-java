package com.javaSDK;

import java.util.Map;
import java.util.Random;

public class fault {

    private static void timeout(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void latency(Experiment experiment) {
        EffectObject effectObject = experiment.effect;
        Map<String,Object> latency = effectObject.latency;
        if (latency == null)
            return;
        if (latency.get("latency") instanceof Integer) {
            timeout((Integer) latency.get("latency"));
        } else if (latency.get("latency") instanceof String) {
            timeout(Integer.parseInt(latency.get("latency").toString()));
        } else if (latency.get("latency") instanceof LatencyObject latencyObject) {
            int ms = latencyObject.ms != null ? latencyObject.ms : 0;
            int jitter = latencyObject.jitter != null ?
                     latencyObject.jitter * new Random().nextInt() : 0;
            timeout(ms + jitter);
        }
    }

    public static void exception(Experiment experiment) {
        EffectObject effectObject = experiment.effect;
        Map<String,Object> exception = effectObject.exception;
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
        if (experiment.effect == null || !(experiment.effect.data instanceof DataObject))
            return prototype;

        PrototypeObject toUse = prototype != null ? prototype : new PrototypeObject();

        DataObject data = (DataObject) experiment.effect.data;
        PrototypeObject res = new PrototypeObject();
        res.copyProperties(toUse);
        res.copyProperties(data);
        return res;
    }

    public static void delayedException(Experiment e) {
        latency(e);
        exception(e);
    }

    public static PrototypeObject delayedDataOrException(Experiment e, PrototypeObject dataPrototype) {
        latency(e);
        exception(e);
        return data(e, dataPrototype);
    }

    public static class EffectObject {
        Map<String, Object> latency;
        Map<String, Object> exception;
        Map<String, Object> data;
    }
    public static class Experiment {
        EffectObject effect;
    }
    public static class LatencyObject {
        Integer ms;
        Integer jitter;
    }
    public static class PrototypeObject {
        Map<String, Object> latency;
        Map<String, Object> exception;
        Map<String, Object> data;
        void copyProperties(PrototypeObject other) {
            this.latency = other.latency;
            this.exception = other.exception;
            this.data = other.data;
        }
    }
    public static class ExceptionObject extends Throwable {
    }

    public static class DataObject extends PrototypeObject {
    }

}