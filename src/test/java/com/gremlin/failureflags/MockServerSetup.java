package com.gremlin.failureflags;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

public class MockServerSetup {
    private static WireMockServer wireMockServer;

    public static void startMockServer() {
        WireMockConfiguration config = WireMockConfiguration.options().port(5032);
        wireMockServer = new WireMockServer(config);
        wireMockServer.start();

        configureFor("localhost", 5032);
        stubFor(post(urlEqualTo("/experiment"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("responses/custom.json"))); // Adjust the path accordingly
    }

    public static void stopMockServer() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }
}