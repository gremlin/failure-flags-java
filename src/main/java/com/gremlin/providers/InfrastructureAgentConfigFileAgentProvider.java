package com.gremlin.providers;

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

public class InfrastructureAgentConfigFileAgentProvider implements FailureFlagAgentProvider {

  private static final Logger logger = LoggerFactory.getLogger(
      InfrastructureAgentConfigFileAgentProvider.class);
  private String fileLocation;

  public InfrastructureAgentConfigFileAgentProvider() {
    this("/etc/gremlin/config.yaml");
  }

  public InfrastructureAgentConfigFileAgentProvider(String configFileLocation) {
    this.fileLocation = configFileLocation;
  }

  @Override
  public GremlinAgent.Builder getGremlinAgentBuilder() {
    logger.info("Using infrastructure agent config file at {}", fileLocation);
    Yaml yaml = new Yaml(new Constructor(InfrastructureAgentYamlConfig.class));
    try {
      InputStream inputStream = new FileInputStream(fileLocation);
      InfrastructureAgentYamlConfig infraYamlConfig = yaml.load(inputStream);
      GremlinAgent.Builder builder = AgentProviderUtils.getBuilder(infraYamlConfig.identifier, null,
          null, null, null, null,
          infraYamlConfig.team_id, infraYamlConfig.team_secret, infraYamlConfig.tags,
          infraYamlConfig.team_certificate, infraYamlConfig.team_private_key);

      return builder;
    } catch (FileNotFoundException e) {
      return new Builder();

    }
  }

  private static class InfrastructureAgentYamlConfig {

    public String identifier;
    public String team_id;
    public Map<String, String> tags;
    public String team_secret;
    public String team_certificate;
    public String team_private_key;
    public String https_proxy;
    public String ssl_cert_file;
    public boolean push_metrics;
    public boolean collect_processes;
  }

}
