package com.gremlin.failureflags;

import com.gremlin.failureflags.exceptions.FailureFlagException;
import com.gremlin.failureflags.models.Experiment;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Faults {
  private static final Logger LOGGER = LoggerFactory.getLogger(Faults.class);

  public void latency(Experiment activeExperiment) {
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

  public void timeout(long ms) {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
  public void delayedException(Experiment activeExperiment) {
    latency(activeExperiment);
    exception(activeExperiment);
  }

  public void exception(Experiment activeExperiment)  {
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
