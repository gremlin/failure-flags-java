package com.gremlin;

import com.gremlin.providers.DefaultFailureFlagAgentProviderChain;
import com.gremlin.providers.FailureFlagAgentProvider;
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
  private String javaVendor;
  private String javaVendorUrl;
  private String javaVersion;
  private String osArch;
  private String osName;
  private String osVersion;
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

  public String getJavaVendor() {
    return javaVendor;
  }

  public String getJavaVendorUrl() {
    return javaVendorUrl;
  }

  public String getJavaVersion() {
    return javaVersion;
  }

  public String getOsArch() {
    return osArch;
  }

  public String getOsName() {
    return osName;
  }

  public String getOsVersion() {
    return osVersion;
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
    //system properties
    this.javaVendor = gremlinAgentBuilder.javaVendor;
    this.javaVendorUrl = gremlinAgentBuilder.javaVendorUrl;
    this.javaVersion = gremlinAgentBuilder.javaVersion;
    this.osArch = gremlinAgentBuilder.osArch;
    this.osName = gremlinAgentBuilder.osName;
    this.osVersion = gremlinAgentBuilder.osVersion;
    //mock properties
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
    private FailureFlagAgentProvider failureFlagAgentProvider;
    //system properties
    private String javaVendor;
    private String javaVendorUrl;
    private String javaVersion;
    private String osArch;
    private String osName;
    private String osVersion;
    //mock properties
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

    public Builder withFailureFlagAgentProvider(FailureFlagAgentProvider failureFlagAgentProvider) {
      this.failureFlagAgentProvider = failureFlagAgentProvider;
      return this;
    }

    //System properties
    public Builder withJavaVendor(final String javaVendor) {
      this.javaVendor = javaVendor;
      return this;
    }

    public Builder withJavaVendorUrl(final String javaVendorUrl) {
      this.javaVendorUrl = javaVendorUrl;
      return this;
    }

    public Builder withJavaVersion(final String javaVersion) {
      this.javaVersion = javaVersion;
      return this;
    }

    public Builder withOsArch(final String osArch) {
      this.osArch = osArch;
      return this;
    }

    public Builder withOsName(final String osName) {
      this.osName = osName;
      return this;
    }

    public Builder withOsVersion(final String osVersion) {
      this.osVersion = osVersion;
      return this;
    }

    public boolean isValidationPassed() {
      //Check for required config settings
      if (this.identifier == null
          || identifier.isEmpty()
          || this.teamId == null
          || this.teamId.isEmpty()
          || ((this.teamSecret == null || this.teamSecret.isEmpty()) && (
          (this.teamCertificate == null
              && this.teamKey == null) || (this.teamCertificate == null
              && this.teamKey != null) || (this.teamCertificate != null
              && this.teamKey == null)))) {
        return false;
      }
      return true;
    }

    public GremlinAgent build() {
      throw new UnsupportedOperationException();
    }

    public GremlinAgent buildMock(final boolean isActive) {
      if (this.failureFlagAgentProvider == null) {
        this.failureFlagAgentProvider = DefaultFailureFlagAgentProviderChain.getInstance();
      }
      GremlinAgent.Builder agentBuilderWithProvider = this.failureFlagAgentProvider.getGremlinAgentBuilder();
      agentBuilderWithProvider.overrideWithNew(this);
      if (agentBuilderWithProvider.isValidationPassed()) {
        agentBuilderWithProvider.isMock = true;
        agentBuilderWithProvider.isActive = isActive;
        return new GremlinAgent(agentBuilderWithProvider);
      }
      throw new IllegalArgumentException("All required fields are not set");
    }

    public void overrideWithNew(Builder newAgentBuilder) {
      if (newAgentBuilder.identifier != null && !newAgentBuilder.identifier.isEmpty()) {
        this.identifier = newAgentBuilder.identifier;
      }
      if (newAgentBuilder.region != null && !newAgentBuilder.region.isEmpty()) {
        this.region = newAgentBuilder.region;
      }
      if (newAgentBuilder.zone != null && !newAgentBuilder.zone.isEmpty()) {
        this.zone = newAgentBuilder.zone;
      }
      if (newAgentBuilder.stage != null && !newAgentBuilder.stage.isEmpty()) {
        this.stage = newAgentBuilder.stage;
      }
      if (newAgentBuilder.version != null && !newAgentBuilder.version.isEmpty()) {
        this.version = newAgentBuilder.version;
      }
      if (newAgentBuilder.build != null && !newAgentBuilder.build.isEmpty()) {
        this.build = newAgentBuilder.build;
      }
      if (newAgentBuilder.tags != null && !newAgentBuilder.tags.isEmpty()) {
        newAgentBuilder.tags.entrySet().stream()
            .forEach(entry -> this.tags.put(entry.getKey(), entry.getValue()));
      }
      if (newAgentBuilder.teamId != null && !newAgentBuilder.teamId.isEmpty()) {
        this.teamId = newAgentBuilder.teamId;
      }
      if (newAgentBuilder.teamSecret != null && !newAgentBuilder.teamSecret.isEmpty()) {
        this.teamSecret = newAgentBuilder.teamSecret;
      }
      if (newAgentBuilder.teamCertificate != null) {
        this.teamCertificate = newAgentBuilder.teamCertificate;
      }
      if (newAgentBuilder.teamKey != null) {
        this.teamKey = newAgentBuilder.teamKey;
      }
      if (newAgentBuilder.javaVendor != null) {
        this.javaVendor = newAgentBuilder.javaVendor;
      }
      if (newAgentBuilder.javaVendorUrl != null) {
        this.javaVendorUrl = newAgentBuilder.javaVendorUrl;
      }
      if (newAgentBuilder.javaVersion != null) {
        this.javaVersion = newAgentBuilder.javaVersion;
      }
      if (newAgentBuilder.osArch != null) {
        this.osArch = newAgentBuilder.osArch;
      }
      if (newAgentBuilder.osName != null) {
        this.osName = newAgentBuilder.osName;
      }
      if (newAgentBuilder.osVersion != null) {
        this.osVersion = newAgentBuilder.osVersion;
      }
    }
  }

  @Override
  public boolean isDependencyTestActive(final String dependencyName, final TestType test) {
    if (isMock) {
      return isActive;
    }
    throw new UnsupportedOperationException();
  }

}
