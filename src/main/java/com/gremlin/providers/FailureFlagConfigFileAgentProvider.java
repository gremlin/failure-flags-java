package com.gremlin.providers;

import static com.gremlin.FailureFlagSDKGlobalConfiguration.GREMLIN_FAILURE_FLAGS_CONFIG_FILE;
import static com.gremlin.FailureFlagSDKGlobalConfiguration.IDENTIFIER;

import com.gremlin.GremlinAgent;
import com.gremlin.GremlinAgent.Builder;
import com.gremlin.utils.AgentProviderUtils;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class FailureFlagConfigFileAgentProvider implements FailureFlagAgentProvider {
  private static final Logger logger = LoggerFactory.getLogger(FailureFlagConfigFileAgentProvider.class);
  private String fileLocation;

  public FailureFlagConfigFileAgentProvider() {
    this(System.getProperty("user.home") == null ? "" : System.getProperty("user.home") + "/.gremlin/config.yaml");
  }

  public FailureFlagConfigFileAgentProvider(String configFileLocation) {
    this.fileLocation = configFileLocation;
    String customConfigFileLocation = System.getenv(GREMLIN_FAILURE_FLAGS_CONFIG_FILE);
    if (customConfigFileLocation != null && !customConfigFileLocation.isEmpty()) {
      this.fileLocation = customConfigFileLocation;
    }
  }


  @Override
  public GremlinAgent.Builder getGremlinAgentBuilder() {
    logger.info("Using failure flag config file at {}", fileLocation);
    Yaml yaml = new Yaml(new Constructor(FailureFlagYamlConfig.class));
    try {
      InputStream inputStream = new FileInputStream(fileLocation);
      FailureFlagYamlConfig failureFlagYamlConfig = yaml.load(inputStream);
      return AgentProviderUtils.getBuilder(failureFlagYamlConfig.identifier, failureFlagYamlConfig.region, failureFlagYamlConfig.zone, failureFlagYamlConfig.stage, failureFlagYamlConfig.version, failureFlagYamlConfig.build,
          failureFlagYamlConfig.teamId, failureFlagYamlConfig.teamSecret, failureFlagYamlConfig.tags, failureFlagYamlConfig.teamCertificate, failureFlagYamlConfig.teamKey);
    } catch (FileNotFoundException e) {
      return new Builder();
    }
  }

  private static class FailureFlagYamlConfig {
    public String identifier;
    public String region;
    public String zone;
    public String stage;
    public String version;
    public String build;
    public Map<String, String> tags;
    public String teamId;
    public String teamSecret;
    public String teamCertificate;
    public String teamKey;
  }

}
