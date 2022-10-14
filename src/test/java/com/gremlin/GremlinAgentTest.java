package com.gremlin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.gremlin.providers.EnvironmentVariableAgentProvider;
import com.gremlin.providers.FailureFlagConfigFileAgentProvider;
import com.gremlin.providers.InfrastructureAgentConfigFileAgentProvider;
import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class GremlinAgentTest {

  private static final String IDENTIFIER = "identifier";
  private static final String REGION = "region";
  private static final String ZONE = "zone";
  private static final String STAGE = "staging";
  private static final String VERSION = "1.0";
  private static final String BUILD = "build";
  private static final String TAG_1_KEY = "key1";
  private static final String TAG_1_VALUE = "value1";
  private static final String TAG_2_KEY = "key2";
  private static final String TAG_2_VALUE = "value2";
  private static final String TAG_3_KEY = "key3";
  private static final String TAG_3_VALUE = "value3";
  private static final String TEAM_ID = "teamId1";
  private static final String TEAM_SECRET = "teamSecret";
  private static final byte[] TEAM_CERT = new byte[]{(byte) 0xea, 0x3a, 0x69, 0x10, (byte) 0xa2,
      (byte) 0xd8, 0x08,
      0x00, 0x2b,
      0x30, 0x30};
  private static final byte[] TEAM_KEY = new byte[]{(byte) 0xd8, 0x08, 0x00, 0x2b,
      0x30, 0x30, (byte) 0x9d};

  private static final String IDENTIFIER_NEW = "identifierNew";
  private static final String REGION_NEW = "regionNew";

  @Test
  void buildGremlinAgent_throwsUnSupportedOperationException() {
    assertThrows(UnsupportedOperationException.class, () ->
        new GremlinAgent.Builder().withIdentifier(IDENTIFIER).build());
  }

  @Test
  void buildMockGremlinAgent_setsAllPropertiesOnGremlinAgent() {
    GremlinAgent gremlinAgent = new GremlinAgent.Builder()
        .withIdentifier(IDENTIFIER)
        .withRegion(REGION)
        .withZone(ZONE)
        .withStage(STAGE)
        .withVersion(VERSION)
        .withBuild(BUILD)
        .withTagPair(TAG_1_KEY, TAG_1_VALUE)
        .withTagPair(TAG_2_KEY, TAG_2_VALUE)
        .withTeamId(TEAM_ID)
        .withTeamSecret(TEAM_SECRET)
        .withTeamCertificate(TEAM_CERT)
        .withTeamKey(TEAM_KEY)
        .buildMock(true);

    Map<String, String> expectedTags = new HashMap<>();
    expectedTags.put(TAG_1_KEY, TAG_1_VALUE);
    expectedTags.put(TAG_2_KEY, TAG_2_VALUE);

    assertEquals(IDENTIFIER, gremlinAgent.getIdentifier());
    assertEquals(REGION, gremlinAgent.getRegion());
    assertEquals(ZONE, gremlinAgent.getZone());
    assertEquals(STAGE, gremlinAgent.getStage());
    assertEquals(VERSION, gremlinAgent.getVersion());
    assertEquals(BUILD, gremlinAgent.getBuild());
    assertEquals(expectedTags, gremlinAgent.getTags());
    assertEquals(TEAM_ID, gremlinAgent.getTeamId());
    assertEquals(TEAM_SECRET, gremlinAgent.getTeamSecret());
    assertEquals(TEAM_CERT, gremlinAgent.getTeamCertificate());
    assertEquals(TEAM_KEY, gremlinAgent.getTeamKey());
  }

  @Test
  void buildMockGremlinAgent_whenIsActiveTrue_returnsTrueForAnyTestAndDependencyTuple() {
    FailureFlags gremlin = new GremlinAgent.Builder()
        .withIdentifier(IDENTIFIER)
        .withRegion(REGION)
        .withZone(ZONE)
        .withStage(STAGE)
        .withVersion(VERSION)
        .withBuild(BUILD)
        .withTagPair(TAG_1_KEY, TAG_1_VALUE)
        .withTagPair(TAG_2_KEY, TAG_2_VALUE)
        .withTeamId(TEAM_ID)
        .withTeamSecret(TEAM_SECRET)
        .withTeamCertificate(TEAM_CERT)
        .withTeamKey(TEAM_KEY)
        .buildMock(true);

    assertEquals(true, gremlin.isDependencyTestActive("dep1", TestType.LATENCY));
    assertEquals(true, gremlin.isDependencyTestActive("dep2", TestType.UNAVAILABLE));
  }

  @Test
  void buildMockGremlinAgent_whenIsActiveFalse_returnsFalseForAnyTestAndDependencyTuple() {
    FailureFlags gremlin = new GremlinAgent.Builder()
        .withIdentifier(IDENTIFIER)
        .withRegion(REGION)
        .withZone(ZONE)
        .withStage(STAGE)
        .withVersion(VERSION)
        .withBuild(BUILD)
        .withTagPair(TAG_1_KEY, TAG_1_VALUE)
        .withTagPair(TAG_2_KEY, TAG_2_VALUE)
        .withTeamId(TEAM_ID)
        .withTeamSecret(TEAM_SECRET)
        .withTeamCertificate(TEAM_CERT)
        .withTeamKey(TEAM_KEY)
        .buildMock(false);

    assertEquals(false, gremlin.isDependencyTestActive("dep1", TestType.LATENCY));
    assertEquals(false, gremlin.isDependencyTestActive("dep2", TestType.UNAVAILABLE));
  }

  @Test
  void buildMockGremlinAgent_whenIdentifierNotPresentAndDefaultProviderChain_returnsFalseOnValidate() {
    GremlinAgent.Builder builder = new GremlinAgent.Builder()
        .withRegion(REGION)
        .withZone(ZONE)
        .withStage(STAGE)
        .withVersion(VERSION)
        .withBuild(BUILD)
        .withTagPair(TAG_1_KEY, TAG_1_VALUE)
        .withTagPair(TAG_2_KEY, TAG_2_VALUE)
        .withTeamId(TEAM_ID)
        .withTeamSecret(TEAM_SECRET)
        .withTeamCertificate(TEAM_CERT)
        .withTeamKey(TEAM_KEY);

    assertFalse(builder.isValidationPassed());
  }

  @Test
  void buildMockGremlinAgent_whenTeamIdNotPresentAndDefaultProviderChain_returnsFalseOnValidate() {
    GremlinAgent.Builder builder = new GremlinAgent.Builder()
        .withIdentifier(IDENTIFIER)
        .withRegion(REGION)
        .withZone(ZONE)
        .withStage(STAGE)
        .withVersion(VERSION)
        .withBuild(BUILD)
        .withTagPair(TAG_1_KEY, TAG_1_VALUE)
        .withTagPair(TAG_2_KEY, TAG_2_VALUE)
        .withTeamSecret(TEAM_SECRET)
        .withTeamCertificate(TEAM_CERT)
        .withTeamKey(TEAM_KEY);

    assertFalse(builder.isValidationPassed());
  }

  @Test
  void buildMockGremlinAgent_whenTeamCredsNotPresentAndDefaultProviderChain_returnsFalseOnValidate() {
    GremlinAgent.Builder builder = new GremlinAgent.Builder()
        .withIdentifier(IDENTIFIER)
        .withRegion(REGION)
        .withZone(ZONE)
        .withStage(STAGE)
        .withVersion(VERSION)
        .withBuild(BUILD)
        .withTagPair(TAG_1_KEY, TAG_1_VALUE)
        .withTagPair(TAG_2_KEY, TAG_2_VALUE);

    assertFalse(builder.isValidationPassed());
  }

  @Test
  void buildMockGremlinAgent_whenTeamCertPresentButNoKeyPresentNotPresentAndDefaultProviderChain_returnsFalseOnValidate() {
    GremlinAgent.Builder builder = new GremlinAgent.Builder()
        .withIdentifier(IDENTIFIER)
        .withRegion(REGION)
        .withZone(ZONE)
        .withStage(STAGE)
        .withVersion(VERSION)
        .withBuild(BUILD)
        .withTagPair(TAG_1_KEY, TAG_1_VALUE)
        .withTagPair(TAG_2_KEY, TAG_2_VALUE)
        .withTeamCertificate(TEAM_CERT);

    assertFalse(builder.isValidationPassed());
  }

  @Test
  void buildMockGremlinAgent_whenTeamKeyPresentButNoCertPresentNotPresentAndDefaultProviderChain_returnsFalseOnValidate() {
    GremlinAgent.Builder builder = new GremlinAgent.Builder()
        .withIdentifier(IDENTIFIER)
        .withRegion(REGION)
        .withZone(ZONE)
        .withStage(STAGE)
        .withVersion(VERSION)
        .withBuild(BUILD)
        .withTagPair(TAG_1_KEY, TAG_1_VALUE)
        .withTagPair(TAG_2_KEY, TAG_2_VALUE)
        .withTeamKey(TEAM_KEY);

    assertFalse(builder.isValidationPassed());
  }

  @Test
  void buildMockGremlinAgent_whenEnvironmentAgentProvider_usesEnvironmentAndOverridesTheRest() {
    EnvironmentVariableAgentProvider environmentVariableAgentProvider = mock(
        EnvironmentVariableAgentProvider.class);
    when(environmentVariableAgentProvider.getGremlinAgentBuilder()).thenReturn(
        new GremlinAgent.Builder()
            .withIdentifier(IDENTIFIER)
            .withRegion(REGION)
            .withZone(ZONE)
            .withStage(STAGE)
            .withVersion(VERSION)
            .withBuild(BUILD)
            .withTagPair(TAG_1_KEY, TAG_1_VALUE)
            .withTagPair(TAG_2_KEY, TAG_2_VALUE)
            .withTeamId(TEAM_ID)
            .withTeamSecret(TEAM_SECRET)
            .withTeamCertificate(TEAM_CERT)
            .withTeamKey(TEAM_KEY));

    GremlinAgent agent = new GremlinAgent.Builder()
        .withFailureFlagAgentProvider(environmentVariableAgentProvider)
        .withIdentifier(IDENTIFIER_NEW)
        .withRegion(REGION_NEW)
        .withTagPair(TAG_3_KEY, TAG_3_VALUE)
        .buildMock(false);

    Map<String, String> expectedTags = new HashMap<>();
    expectedTags.put(TAG_1_KEY, TAG_1_VALUE);
    expectedTags.put(TAG_2_KEY, TAG_2_VALUE);
    expectedTags.put(TAG_3_KEY, TAG_3_VALUE);

    assertEquals(IDENTIFIER_NEW, agent.getIdentifier());
    assertEquals(REGION_NEW, agent.getRegion());
    assertEquals(ZONE, agent.getZone());
    assertEquals(STAGE, agent.getStage());
    assertEquals(VERSION, agent.getVersion());
    assertEquals(BUILD, agent.getBuild());
    assertEquals(expectedTags, agent.getTags());
    assertEquals(TEAM_ID, agent.getTeamId());
    assertEquals(TEAM_SECRET, agent.getTeamSecret());
    assertEquals(TEAM_CERT, agent.getTeamCertificate());
    assertEquals(TEAM_KEY, agent.getTeamKey());
  }

  @Test
  public void buildMockGremlinAgent_whenInfraAgentConfigFileProvider() throws URISyntaxException {
    File file = new File("src/test/resources/test-infra-config.yaml");
    String absolutePath = file.getAbsolutePath();

    GremlinAgent agent = new GremlinAgent.Builder()
        .withFailureFlagAgentProvider(new InfrastructureAgentConfigFileAgentProvider(absolutePath))
        .withIdentifier(IDENTIFIER_NEW)
        .withRegion(REGION_NEW)
        .withTagPair(TAG_3_KEY, TAG_3_VALUE)
        .buildMock(false);

    Map<String, String> expectedTags = new HashMap<>();
    expectedTags.put("service", "pet-store");
    expectedTags.put("interface", "http");
    expectedTags.put(TAG_3_KEY, TAG_3_VALUE);

    assertEquals(IDENTIFIER_NEW, agent.getIdentifier());
    assertEquals(REGION_NEW, agent.getRegion());
    assertNull(agent.getZone());
    assertNull(agent.getStage());
    assertNull(agent.getVersion());
    assertNull(agent.getBuild());
    assertEquals(expectedTags, agent.getTags());
    assertEquals("11111111-1111-1111-1111-111111111111", agent.getTeamId());
    assertNull(agent.getTeamSecret());
    assertNotNull(agent.getTeamCertificate());
    assertNotNull(agent.getTeamKey());

  }

  @Test
  public void buildMockGremlinAgent_whenFailureFlagConfigFileProvider_usesFailureFlagConfigAndOverridesTheRest() throws URISyntaxException {
    File file = new File("src/test/resources/test-failure-flag-config.yaml");
    String absolutePath = file.getAbsolutePath();

    GremlinAgent agent = new GremlinAgent.Builder()
        .withFailureFlagAgentProvider(new FailureFlagConfigFileAgentProvider(absolutePath))
        .withIdentifier(IDENTIFIER_NEW)
        .withRegion(REGION_NEW)
        .withTagPair(TAG_3_KEY, TAG_3_VALUE)
        .buildMock(false);

    Map<String, String> expectedTags = new HashMap<>();
    expectedTags.put("app", "control");
    expectedTags.put(TAG_3_KEY, TAG_3_VALUE);

    assertEquals(IDENTIFIER_NEW, agent.getIdentifier());
    assertEquals(REGION_NEW, agent.getRegion());
    assertEquals("us-west-1a", agent.getZone());
    assertNull(agent.getStage());
    assertNull(agent.getVersion());
    assertEquals("2.3.0", agent.getBuild());
    assertEquals(expectedTags, agent.getTags());
    assertEquals("otherteam", agent.getTeamId());
    assertEquals("somesecret", agent.getTeamSecret());
    assertNull(agent.getTeamCertificate());
    assertNull(agent.getTeamKey());
  }
}
