package com.gremlin.failureflags;

@FunctionalInterface
public interface Behavior {
  void applyBehavior(Experiment[] experiments);
}
