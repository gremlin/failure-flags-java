package com.gremlin.providers;

import com.gremlin.GremlinAgent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultFailureFlagAgentProviderChain implements FailureFlagAgentProvider {

  private static final Logger logger = LoggerFactory.getLogger(
      DefaultFailureFlagAgentProviderChain.class);

  private static final DefaultFailureFlagAgentProviderChain INSTANCE = new DefaultFailureFlagAgentProviderChain();
  private final List<FailureFlagAgentProvider> failureFlagAgentProviderList =
      new ArrayList<>();

  public DefaultFailureFlagAgentProviderChain() {
    failureFlagAgentProviderList.addAll(
        Arrays.asList(new InfrastructureAgentConfigFileAgentProvider(),
            new FailureFlagConfigFileAgentProvider(), new SystemPropertiesAgentProvider(),
            new EnvironmentVariableAgentProvider()));
  }

  public static DefaultFailureFlagAgentProviderChain getInstance() {
    return INSTANCE;
  }

  @Override
  public GremlinAgent.Builder getGremlinAgentBuilder() {
    logger.info("Using default Failure Flag provider chain");
    GremlinAgent.Builder originalBuilder = new GremlinAgent.Builder();
    for (FailureFlagAgentProvider provider : failureFlagAgentProviderList) {
      try {
        GremlinAgent.Builder newAgentBuilder = provider.getGremlinAgentBuilder();
        originalBuilder.overrideWithNew(newAgentBuilder);
      }  catch (Exception e) {
        logger.info(e.getMessage());
      }
    }
    return originalBuilder;
  }

}
