package com.gremlin.failureflags;

import com.gremlin.failureflags.models.Experiment;

@FunctionalInterface
public interface Behavior {
  void applyBehavior(Experiment activeExperiment);
}
