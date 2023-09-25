package com.gremlin.failureflags.behaviors;

import com.gremlin.failureflags.Behavior;
import com.gremlin.failureflags.FailureFlagException;
import com.gremlin.failureflags.Experiment;

public class Exception implements Behavior {
  public void applyBehavior(Experiment[] experiments) {
    for (Experiment e: experiments) {
      if (!e.getEffect().containsKey("exception")) {
        continue;
      }
      Object failureFlagException = e.getEffect().get("exception");
      if (failureFlagException instanceof String) {
        throw new FailureFlagException("Exception injected by failure flag: " + failureFlagException);
      }
    }
  }
}