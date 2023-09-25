package com.gremlin.failureflags.behaviors;

import com.gremlin.failureflags.Behavior;
import com.gremlin.failureflags.FailureFlagException;
import com.gremlin.failureflags.Experiment;
/**
 * Exception processes <code>exception</code> properties in experiment effects.
 *
 * Behaviors implement specific effects or symptoms of failures that an application will experience in calls to
 * FailureFlags.invoke(...). When processing multiple experiments, delays should be applied before other failure types
 * and those failure types that can be processed without changing flow should be applied first. If multiple experiments
 * result in changing control flow (like exceptions, shutdowns, panics, etc.) then the behavior chain may not realize
 * some effects.
 * */
public class Exception implements Behavior {
  /**{@inheritDoc}*/
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