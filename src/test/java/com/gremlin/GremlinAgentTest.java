package com.gremlin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
  private static final String TAG_2_VALUE = "zone";
  private static final String TEAM_ID = "teamId1";
  private static final String TEAM_SECRET = "teamSecret";
  private static final byte[] TEAM_CERT = new byte[]{(byte) 0xea, 0x3a, 0x69, 0x10, (byte) 0xa2,
      (byte) 0xd8, 0x08,
      0x00, 0x2b,
      0x30, 0x30};
  private static final byte[] TEAM_KEY = new byte[]{(byte) 0xd8, 0x08, 0x00, 0x2b,
      0x30, 0x30, (byte) 0x9d};

  @Test
  public void buildGremlinAgent_throwsUnSupportedOperationException() {
    assertThrows(UnsupportedOperationException.class, () ->
        new GremlinAgent.Builder().withIdentifier(IDENTIFIER).build());
  }

  @Test
  public void buildMockGremlinAgent_setsAllPropertiesOnGremlinAgent() {
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
  public void buildMockGremlinAgent_whenIsActiveTrue_returnsTrueForAnyTestAndDependencyTuple() {
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
  public void buildMockGremlinAgent_whenIsActiveFalse_returnsFalseForAnyTestAndDependencyTuple() {
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
}
