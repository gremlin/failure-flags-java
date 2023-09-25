package com.gremlin.failureflags;

/**
 * NoopFailureFlags is a stub implementation of FailureFlags. It does nothing and its methods always return null.
 * */
public class NoopFailureFlags implements FailureFlags {
    @Override
    public Experiment[] invoke(FailureFlag flag, Behavior behavior) {
        return null;
    }

    @Override
    public Experiment[] invoke(FailureFlag flag) {
        return null;
    }

    @Override
    public Experiment[] fetch(FailureFlag flag) {
        return null;
    }
}
