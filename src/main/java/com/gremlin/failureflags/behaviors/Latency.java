package com.gremlin.failureflags.behaviors;

import com.gremlin.failureflags.Behavior;
import com.gremlin.failureflags.exceptions.FailureFlagException;
import com.gremlin.failureflags.models.Experiment;
import java.util.Map;

public class Latency implements Behavior {

  public void applyBehavior(Experiment activeExperiment) {
    if (activeExperiment.getEffect().containsKey("latency")) {
      Object latencyObject = activeExperiment.getEffect().get("latency");
      if (latencyObject instanceof String || latencyObject instanceof Integer) {
        try {
          int latencyToInject = Integer.parseInt(activeExperiment.getEffect().get("latency").toString());
          timeout(latencyToInject);
          return;
        } catch (NumberFormatException nfe) {
          throw new FailureFlagException("Invalid value for latency passed");
        }
      }
      if (latencyObject instanceof Object) {
        Map<String, String> latencyMap = (Map<String, String>) latencyObject;
        Object ms = latencyMap.get("ms");
        Object jitter = latencyMap.get("jitter");
        if ((ms instanceof String || ms instanceof Integer) && (jitter instanceof String || jitter instanceof Integer)) {
          try {
            int latencyToInject = Integer.parseInt(ms.toString());
            long jitterMs = Integer.parseInt(jitter.toString());
            timeout(latencyToInject + (jitterMs == 0 ? 0 : (long)Math.random() * jitterMs));
          } catch (NumberFormatException nfe) {
            throw new FailureFlagException("Invalid value for latency passed");
          }
        }
      }
    }
  }

  private void timeout(long ms) {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

}
