package com.gremlin.failureflags.behaviors;

import com.gremlin.failureflags.Behavior;
import com.gremlin.failureflags.models.Experiment;

public class DelayedException implements Behavior {
  public void applyBehavior(Experiment activeExperiment) {
    new Latency().applyBehavior(activeExperiment);
    new Exception().applyBehavior(activeExperiment);
  }
}
