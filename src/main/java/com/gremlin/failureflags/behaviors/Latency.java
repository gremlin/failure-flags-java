package com.gremlin.failureflags.behaviors;

import com.gremlin.failureflags.Behavior;
import com.gremlin.failureflags.FailureFlagException;
import com.gremlin.failureflags.Experiment;
import java.util.Map;

/**
 * Latency calls Thread.sleep for some duration as specified by the "latency" property in an Effect statement for each
 * experiment in a provided list of experiments. This implementation supports the following statement forms:
 *
 * 1. An object form with two properties, "ms" and or "jitter" where each is an integer in milliseconds.
 * 2. A string containing an integer representing a consistent number of milliseconds to delay.
 * 3. An integer representing a consistent number of milliseconds to delay.
 *
 * For example:
 * {
 *  ...
 *   "latency": {
 *       "ms": 1000,
 *       "jitter": 100
 *   }
 *  ...
 * }
 *
 * or
 *
 * {
 *  ...
 *   "latency": 1000
 *  ...
 * }
 *
 * or
 *
 * {
 *  ...
 *   "latency": "1000"
 *  ...
 * }
 * */
public class Latency implements Behavior {

  /**{@inheritDoc}*/
  public void applyBehavior(Experiment[] experiments) {
    for (Experiment e: experiments) {
      if (!e.getEffect().containsKey("latency")) { continue; }
      Object latencyObject = e.getEffect().get("latency");
      if (latencyObject instanceof String || latencyObject instanceof Integer) {
        try {
          int latencyToInject = Integer.parseInt(e.getEffect().get("latency").toString());
          timeout(latencyToInject);
          continue;
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
