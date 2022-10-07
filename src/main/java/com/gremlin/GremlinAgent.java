package com.gremlin;

import java.util.HashMap;
import java.util.Map;

public class GremlinAgent implements FailureFlags {

  private String identifier;
  private String region;
  private String zone;
  private String stage;
  private String version;
  private String build;
  private Map<String, String> tags;
  private String teamId;
  private String teamSecret;
  private byte[] teamCertificate;
  private byte[] teamKey;
  private boolean isMock;
  private boolean isActive;

  public String getIdentifier() {
    return identifier;
  }

  public String getRegion() {
    return region;
  }

  public String getZone() {
    return zone;
  }

  public String getStage() {
    return stage;
  }

  public String getVersion() {
    return version;
  }

  public String getBuild() {
    return build;
  }

  public Map<String, String> getTags() {
    return tags;
  }

  public String getTeamId() {
    return teamId;
  }

  public String getTeamSecret() {
    return teamSecret;
  }

  public byte[] getTeamCertificate() {
    return teamCertificate;
  }

  public byte[] getTeamKey() {
    return teamKey;
  }

  public boolean isMock() {
    return isMock;
  }

  private GremlinAgent(GremlinAgent.Builder gremlinAgentBuilder) {
    this.identifier = gremlinAgentBuilder.identifier;
    this.region = gremlinAgentBuilder.region;
    this.zone = gremlinAgentBuilder.zone;
    this.stage = gremlinAgentBuilder.stage;
    this.version = gremlinAgentBuilder.version;
    this.build = gremlinAgentBuilder.build;
    this.tags = gremlinAgentBuilder.tags;
    this.teamId = gremlinAgentBuilder.teamId;
    this.teamSecret = gremlinAgentBuilder.teamSecret;
    this.teamCertificate = gremlinAgentBuilder.teamCertificate;
    this.teamKey = gremlinAgentBuilder.teamKey;
    this.isMock = gremlinAgentBuilder.isMock;
    this.isActive = gremlinAgentBuilder.isActive;
  }

  public static class Builder {

    private String identifier;
    private String region;
    private String zone;
    private String stage;
    private String version;
    private String build;
    private Map<String, String> tags = new HashMap<>();
    private String teamId;
    private String teamSecret;
    private byte[] teamCertificate;
    private byte[] teamKey;
    private boolean isActive;
    private boolean isMock;

    public Builder withIdentifier(final String identifier) {
      this.identifier = identifier;
      return this;
    }

    public Builder withRegion(final String region) {
      this.region = region;
      return this;
    }

    public Builder withZone(final String zone) {
      this.zone = zone;
      return this;
    }

    public Builder withStage(final String stage) {
      this.stage = stage;
      return this;
    }

    public Builder withVersion(final String version) {
      this.version = version;
      return this;
    }

    public Builder withBuild(final String build) {
      this.build = build;
      return this;
    }

    public Builder withTagPair(final String tagKey, final String tagValue) {
      this.tags.put(tagKey, tagValue);
      return this;
    }

    public Builder withTeamId(final String teamId) {
      this.teamId = teamId;
      return this;
    }

    public Builder withTeamSecret(final String teamSecret) {
      this.teamSecret = teamSecret;
      return this;
    }

    public Builder withTeamCertificate(final byte[] teamCertificate) {
      this.teamCertificate = teamCertificate;
      return this;
    }

    public Builder withTeamKey(final byte[] teamKey) {
      this.teamKey = teamKey;
      return this;
    }

    public GremlinAgent build() {
      throw new UnsupportedOperationException();
    }

    public GremlinAgent buildMock(boolean isActive) {
      this.isMock = true;
      this.isActive = isActive;
      return new GremlinAgent(this);
    }
  }

  @Override
  public boolean isDependencyTestActive(String dependencyName, TestType test) {
    if (isMock) {
      return isActive;
    }
    throw new UnsupportedOperationException();
  }

}
