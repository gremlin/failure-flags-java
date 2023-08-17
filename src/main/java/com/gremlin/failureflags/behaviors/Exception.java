package com.gremlin.failureflags.behaviors;

import com.gremlin.failureflags.Behavior;
import com.gremlin.failureflags.exceptions.FailureFlagException;
import com.gremlin.failureflags.models.Experiment;
import java.util.Map;

public class Exception implements Behavior {
  public void applyBehavior(Experiment activeExperiment) {
    if (!activeExperiment.getEffect().containsKey("exception")) {
      return;
    }
    Object failureFlagException = activeExperiment.getEffect().get("exception");
    if (failureFlagException instanceof String) {
      throw new FailureFlagException("Exception injected by failure flag: " + failureFlagException);
    }
    else if (failureFlagException instanceof Map) {
      Map<String, String> exceptionMap = (Map<String, String>) failureFlagException;
      if (exceptionMap.containsKey("message") && exceptionMap.containsKey("name")) {
        throw new FailureFlagException(String.format("Exception injected by failure flag: message: {}, name: {}", exceptionMap.get("message"), exceptionMap.get("name")));
      }
    }
  }
}
