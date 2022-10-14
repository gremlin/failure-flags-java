package com.gremlin.providers;

import static com.gremlin.FailureFlagSDKGlobalConfiguration.BUILD;
import static com.gremlin.FailureFlagSDKGlobalConfiguration.IDENTIFIER;
import static com.gremlin.FailureFlagSDKGlobalConfiguration.JAVA_VENDOR;
import static com.gremlin.FailureFlagSDKGlobalConfiguration.JAVA_VENDOR_URL;
import static com.gremlin.FailureFlagSDKGlobalConfiguration.JAVA_VERSION;
import static com.gremlin.FailureFlagSDKGlobalConfiguration.OS_ARCH;
import static com.gremlin.FailureFlagSDKGlobalConfiguration.OS_NAME;
import static com.gremlin.FailureFlagSDKGlobalConfiguration.OS_VERSION;
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
import com.gremlin.GremlinAgent.Builder;
import com.gremlin.utils.AgentProviderUtils;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemPropertiesAgentProvider implements FailureFlagAgentProvider {

  private static final Logger logger = LoggerFactory.getLogger(
      SystemPropertiesAgentProvider.class);

  private String fileLocation;

  public SystemPropertiesAgentProvider() {
  }

  public SystemPropertiesAgentProvider(String propertiesFile) {
    this.fileLocation = propertiesFile;
  }

  @Override
  public GremlinAgent.Builder getGremlinAgentBuilder() {
    logger.info("Using system properties {}", fileLocation == null ? "" : "at " + fileLocation);
    try {
      if (fileLocation != null && !fileLocation.isEmpty()) {
        FileInputStream propFile =
            new FileInputStream(fileLocation);
        Properties p =
            new Properties(System.getProperties());
        p.load(propFile);

        // set the system properties
        System.setProperties(p);
      }

      String identifier = System.getProperty(IDENTIFIER);
      String region = System.getProperty(REGION);
      String zone = System.getProperty(ZONE);
      String stage = System.getProperty(STAGE);
      String version = System.getProperty(VERSION);
      String build = System.getProperty(BUILD);
      Map<String, String> tags = AgentProviderUtils.getTags(System.getProperty(TAGS));
      String teamId = System.getProperty(TEAM_ID);
      String teamSecret = System.getProperty(TEAM_SECRET);
      String teamCertificate = System.getProperty(TEAM_CERTIFICATE);
      String teamKey = System.getProperty(TEAM_KEY);

      GremlinAgent.Builder builder = AgentProviderUtils.getBuilder(identifier, region, zone, stage,
          version, build, teamId, teamSecret, tags, teamCertificate, teamKey);

      builder.withJavaVendor(System.getProperty(JAVA_VENDOR))
          .withJavaVendorUrl(System.getProperty(JAVA_VENDOR_URL))
          .withJavaVersion(System.getProperty(JAVA_VERSION))
          .withOsArch(System.getProperty(OS_ARCH))
          .withOsName(System.getProperty(OS_NAME))
          .withOsVersion(System.getProperty(OS_VERSION));

      return builder;

    } catch (FileNotFoundException e) {
      return new Builder();
    } catch (IOException e) {
      return new Builder();
    }
  }

}
