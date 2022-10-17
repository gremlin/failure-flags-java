package com.gremlin;

public class FailureFlagSDKGlobalConfiguration {
  private FailureFlagSDKGlobalConfiguration() {
    throw new IllegalStateException("Utility class");
  }

  //Failure flag config - env vars or system properties
  public static final String IDENTIFIER = "IDENTIFIER";
  public static final String REGION = "REGION";
  public static final String ZONE = "ZONE";
  public static final String STAGE = "STAGE";
  public static final String VERSION = "VERSION";
  public static final String BUILD = "BUILD";
  public static final String TAGS = "TAGS";
  public static final String TEAM_ID = "TEAM_ID";
  public static final String TEAM_SECRET = "TEAM_SECRET";
  public static final String TEAM_CERTIFICATE = "TEAM_CERTIFICATE";
  public static final String TEAM_KEY = "TEAM_KEY";

  //Failure flag config file location
  public static final String GREMLIN_FAILURE_FLAGS_CONFIG_FILE = "GREMLIN_FAILURE_FLAGS_CONFIG_FILE";

  //System properties
  public static final String JAVA_VENDOR = "java.vendor";
  public static final String JAVA_VENDOR_URL = "java.vendor.url";
  public static final String JAVA_VERSION = "java.version";
  public static final String OS_ARCH = "os.arch";
  public static final String OS_NAME = "os.name";
  public static final String OS_VERSION = "os.version";
}
