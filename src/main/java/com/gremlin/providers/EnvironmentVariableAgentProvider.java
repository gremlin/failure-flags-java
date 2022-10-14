package com.gremlin.providers;

import static com.gremlin.FailureFlagSDKGlobalConfiguration.BUILD;
import static com.gremlin.FailureFlagSDKGlobalConfiguration.IDENTIFIER;
import static com.gremlin.FailureFlagSDKGlobalConfiguration.REGION;
import static com.gremlin.FailureFlagSDKGlobalConfiguration.STAGE;
import static com.gremlin.FailureFlagSDKGlobalConfiguration.TAGS;
import static com.gremlin.FailureFlagSDKGlobalConfiguration.TEAM_CERTIFICATE;
import static com.gremlin.FailureFlagSDKGlobalConfiguration.TEAM_ID;
import static com.gremlin.FailureFlagSDKGlobalConfiguration.TEAM_KEY;
import static com.gremlin.FailureFlagSDKGlobalConfiguration.TEAM_SECRET;
import static com.gremlin.FailureFlagSDKGlobalConfiguration.VERSION;
import static com.gremlin.FailureFlagSDKGlobalConfiguration.ZONE;

import com.gremlin.GremlinAgent;
import com.gremlin.utils.AgentProviderUtils;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EnvironmentVariableAgentProvider implements FailureFlagAgentProvider {
  private static final Logger logger = LoggerFactory.getLogger(EnvironmentVariableAgentProvider.class);

  @Override
  public GremlinAgent.Builder getGremlinAgentBuilder() {
    logger.info("Using environment variables");
    String identifier = System.getenv(IDENTIFIER);
    String region = System.getenv(REGION);
    String zone = System.getenv(ZONE);
    String stage = System.getenv(STAGE);
    String version = System.getenv(VERSION);
    String build = System.getenv(BUILD);
    Map<String, String> tags = AgentProviderUtils.getTags(System.getenv(TAGS));
    String teamId = System.getenv(TEAM_ID);
    String teamSecret = System.getenv(TEAM_SECRET);
    String teamCertificate = System.getenv(TEAM_CERTIFICATE);
    String teamKey = System.getenv(TEAM_KEY);

    GremlinAgent.Builder builder = AgentProviderUtils.getBuilder(identifier, region, zone, stage, version, build, teamId, teamSecret, tags, teamCertificate, teamKey);
    return builder;
  }
}
