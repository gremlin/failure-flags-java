package com.javaSDK;

import java.io.IOException;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;


class FetchTest {
    private static final String TEST_NAME = "test_experiment";
    private static final int TEST_PORT = 8080;
    private static WireMockServer wireMockServer;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(TEST_PORT);
        wireMockServer.start();
        fetch.setPort(TEST_PORT);
    }

    @AfterEach
    void takeDown(){
        wireMockServer.stop();
    }



    @Test
    void testFetchExperimentSuccess() throws IOException {

        configureFor("localhost", TEST_PORT);

        stubFor(post(urlEqualTo("/experiment"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"result\": \"success\"}")));

        String result = fetch.fetchExperiment(TEST_NAME, "http://localhost:" + TEST_PORT);

        assertEquals("{\"result\": \"success\"}", result);

        wireMockServer.stop();
    }

    @Test
    void testFetchExperimentFailure() {
        configureFor("localhost", TEST_PORT);

        stubFor(post(urlEqualTo("/experiment"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withBody("Bad Request")));

        assertThrows(IOException.class, () -> {
            fetch.fetchExperiment(TEST_NAME, "http://localhost:" + TEST_PORT + "/experiment");
        });
    }
}