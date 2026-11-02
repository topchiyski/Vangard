package com.vanguard.core.xray;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.vanguard.core.report.CucumberJsonMergerTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

/** Proves the Xray exporter talks to Xray correctly, using a local fake Xray server. */
public class XrayResultsExporterTest {

    private WireMockServer xray;

    @BeforeClass
    public void startFakeXray() {
        xray = new WireMockServer(options().dynamicPort());
        xray.start();
    }

    @AfterClass(alwaysRun = true)
    public void stopFakeXray() {
        xray.stop();
    }

    @BeforeMethod
    public void reset() {
        xray.resetAll();
    }

    @Test
    public void cloudAuthenticatesThenImportsDeduplicatedResults() {
        xray.stubFor(post("/api/v2/authenticate")
                .withRequestBody(equalToJson("{\"client_id\":\"id-1\",\"client_secret\":\"secret-1\"}"))
                .willReturn(aResponse().withStatus(200).withBody("\"token-123\"")));
        xray.stubFor(post("/api/v2/import/execution/cucumber")
                .withHeader("Authorization", equalTo("Bearer token-123"))
                .willReturn(aResponse().withStatus(200)
                        .withBody("{\"id\":\"1\",\"key\":\"QA-500\",\"self\":\"https://jira/QA-500\"}")));

        Optional<String> key = exporter("cloud").upload(CucumberJsonMergerTest.RETRIED_REPORT);

        assertThat(key).contains("QA-500");
        List<LoggedRequest> imports = xray.findAll(postRequestedFor(urlEqualTo("/api/v2/import/execution/cucumber")));
        assertThat(imports).hasSize(1);
        String uploaded = imports.get(0).getBodyAsString();
        // The retried scenario must appear once, with the passing attempt only.
        assertThat(uploaded.split("\"type\" : \"scenario\"", -1)).hasSize(2);
        assertThat(uploaded).doesNotContain("\"failed\"");
    }

    @Test
    public void dataCenterUsesPersonalAccessToken() {
        xray.stubFor(post("/rest/raven/2.0/import/execution/cucumber")
                .withHeader("Authorization", equalTo("Bearer pat-xyz"))
                .willReturn(aResponse().withStatus(200)
                        .withBody("{\"testExecIssue\":{\"id\":\"9\",\"key\":\"QA-77\"}}")));

        assertThat(exporter("datacenter").upload(CucumberJsonMergerTest.RETRIED_REPORT)).contains("QA-77");
        xray.verify(0, postRequestedFor(urlEqualTo("/api/v2/authenticate")));
    }

    @Test
    public void failedUploadNeverThrows() {
        xray.stubFor(post("/api/v2/authenticate").willReturn(aResponse().withStatus(401)));

        assertThat(exporter("cloud").upload(CucumberJsonMergerTest.RETRIED_REPORT)).isEmpty();
    }

    private XrayResultsExporter exporter(String mode) {
        return new XrayResultsExporter(new XraySettings(mode, xray.baseUrl(), "id-1", "secret-1", "pat-xyz", "QA"));
    }
}
