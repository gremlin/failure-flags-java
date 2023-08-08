package com.javaSDK;

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
        if (experiment.effect == null || experiment.effect.latency == null)
            return;

        Object latency = experiment.effect.latency;
        if (latency instanceof Integer) {
            timeout((Integer) latency);
        } else if (latency instanceof String) {
            timeout(Integer.parseInt((String) latency));
        } else if (latency instanceof LatencyObject latencyObject) {
            int ms = latencyObject.ms != null ? (Integer) latencyObject.ms : 0;
            int jitter = latencyObject.jitter != null ?
                    (Integer) latencyObject.jitter * new Random().nextInt() : 0;
            timeout(ms + jitter);
        }
    }

    public static void exception(Experiment experiment) {
        if (experiment.effect == null || experiment.effect.exception == null)
            return;

        Object exception = experiment.effect.exception;
        if (exception instanceof String) {
            throw new RuntimeException((String) exception);
        } else if (exception instanceof ExceptionObject exceptionObject) {
            RuntimeException toThrow = new RuntimeException("Exception injected by Failure Flags");
            toThrow.initCause(exceptionObject);
            throw toThrow;
        }
    }

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
        Object latency;
        Object exception;
        Object data;
    }
    public static class Experiment {
        EffectObject effect;
    }
    public static class LatencyObject {
        Integer ms;
        Integer jitter;
    }
    public static class PrototypeObject {
        void copyProperties(PrototypeObject other) {
            // Copy properties from 'other' to 'this'
        }
    }
    public static class ExceptionObject extends Throwable {
    }

    public static class DataObject extends PrototypeObject {
    }

}