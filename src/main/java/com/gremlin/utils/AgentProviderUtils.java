package com.gremlin.utils;

import com.gremlin.GremlinAgent;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class AgentProviderUtils {

  private AgentProviderUtils() {
    throw new IllegalStateException("Utility class");
  }

  public static Map<String, String> getTags(String tags) {
    Map<String, String> tagsMap = new HashMap<>();
    if (tags != null) {
      String[] keyPairs = tags.split(",");
      if (keyPairs.length > 0) {
        Arrays.asList(keyPairs).forEach(kp -> {
          String[] keyPair = kp.trim().split("=");
          if (keyPair.length == 2) {
            tagsMap.put(keyPair[0], keyPair[1]);
          }
        });
      }
    }
    return tagsMap;
  }

  private static void populateBuilderWithTags(Map<String, String> tags,
      GremlinAgent.Builder builder) {
    if (tags != null) {
      for (Entry<String, String> keyValue : tags.entrySet()) {
        builder.withTagPair(keyValue.getKey(), keyValue.getValue());
      }
    }
  }

  private static void populateBuilderWithTeamCertAndKey(String teamCertificate, String teamKey,
      GremlinAgent.Builder builder) {
    if (teamCertificate != null) {
      builder.withTeamCertificate(teamCertificate.getBytes(StandardCharsets.UTF_8));
    }
    if (teamKey != null) {
      builder.withTeamKey(teamKey.getBytes(StandardCharsets.UTF_8));
    }
  }

  public static GremlinAgent.Builder getBuilder(String identifier, String region, String zone,
      String stage, String version, String build, String teamId, String teamSecret, Map<String, String> tags,
      String teamCertificate, String teamKey) {
    GremlinAgent.Builder builder = new GremlinAgent.Builder()
        .withIdentifier(identifier)
        .withRegion(region)
        .withZone(zone)
        .withStage(stage)
        .withVersion(version)
        .withBuild(build)
        .withTeamId(teamId)
        .withTeamSecret(teamSecret);

    populateBuilderWithTags(tags, builder);
    populateBuilderWithTeamCertAndKey(teamCertificate, teamKey, builder);
    return builder;
  }


}
