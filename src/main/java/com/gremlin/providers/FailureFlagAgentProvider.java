package com.gremlin.providers;

import com.gremlin.GremlinAgent;

public interface FailureFlagAgentProvider {
  GremlinAgent.Builder getGremlinAgentBuilder();
}
