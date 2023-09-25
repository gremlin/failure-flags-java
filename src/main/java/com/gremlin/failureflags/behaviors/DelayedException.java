package com.gremlin.failureflags.behaviors;

import com.gremlin.failureflags.Behavior;
import com.gremlin.failureflags.Experiment;

public class DelayedException implements Behavior {
  public void applyBehavior(Experiment[] experiments) {
    new Latency().applyBehavior(experiments);
    new Exception().applyBehavior(experiments);
  }
}
