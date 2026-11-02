package com.vanguard.core.xray;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.vanguard.core.report.CucumberJsonMerger;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;

/**
 * Uploads Cucumber results to Jira Xray and creates ONE Test Execution.
 *
 * <ul>
 *   <li><b>Cloud</b>: POST /api/v2/authenticate (client id + secret → token), then
 *       POST /api/v2/import/execution/cucumber with {@code Authorization: Bearer <token>}.</li>
 *   <li><b>Data Center</b>: POST /rest/raven/2.0/import/execution/cucumber with a personal access token.</li>
 * </ul>
 *
 * <p>Scenarios are linked to Jira Tests through tags like {@code @TEST_QA-101}.
 * An upload failure is logged as ERROR but never throws: reporting must not hide test results.
 */
@Slf4j
public class XrayResultsExporter {

    static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(10);
    static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(60);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final XraySettings settings;
    private final HttpClient http;

    public XrayResultsExporter(XraySettings settings) {
        // WHY: HTTP/1.1 avoids the h2c upgrade on plain http:// that proxies and mock servers mishandle.
        this(settings, HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(CONNECT_TIMEOUT)
                .build());
    }

    public XrayResultsExporter(XraySettings settings, HttpClient http) {
        this.settings = settings;
        this.http = http;
    }

    /**
     * Deduplicates retried scenarios and uploads the report.
     *
     * @param cucumberJson the content of a cucumber.json report
     * @return the created Test Execution key (e.g. {@code QA-123}), or empty when the upload failed
     */
    public Optional<String> upload(String cucumberJson) {
        try {
            ArrayNode report = CucumberJsonMerger.deduplicate(cucumberJson);
            warnAboutUnlinkedScenarios(report);
            String body = CucumberJsonMerger.toJson(report);
            HttpResponse<String> response = settings.isCloud() ? uploadToCloud(body) : uploadToDataCenter(body);
            if (response.statusCode() / 100 != 2) {
                log.error("Xray import failed: HTTP {} - {}", response.statusCode(), response.body());
                return Optional.empty();
            }
            Optional<String> key = executionKey(response.body());
            key.ifPresentOrElse(
                    k -> log.info("Xray Test Execution: {}", k),
                    () -> log.warn("Xray import succeeded but no Test Execution key was returned: {}", response.body()));
            return key;
        } catch (IOException | RuntimeException e) {
            log.error("Xray upload failed ({}): {}", settings, e.getMessage());
            return Optional.empty();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Xray upload interrupted");
            return Optional.empty();
        }
    }

    private HttpResponse<String> uploadToCloud(String body) throws IOException, InterruptedException {
        if (settings.clientId().isBlank() || settings.clientSecret().isBlank()) {
            throw new IllegalStateException("Xray Cloud needs XRAY_CLIENT_ID and XRAY_CLIENT_SECRET");
        }
        String token = authenticate();
        return post("/api/v2/import/execution/cucumber", body, token);
    }

    private HttpResponse<String> uploadToDataCenter(String body) throws IOException, InterruptedException {
        if (settings.token().isBlank()) {
            throw new IllegalStateException("Xray Data Center needs a personal access token in XRAY_TOKEN");
        }
        return post("/rest/raven/2.0/import/execution/cucumber", body, settings.token());
    }

    private String authenticate() throws IOException, InterruptedException {
        String credentials = MAPPER.writeValueAsString(Map.of(
                "client_id", settings.clientId(),
                "client_secret", settings.clientSecret()));
        HttpResponse<String> response = post("/api/v2/authenticate", credentials, null);
        if (response.statusCode() / 100 != 2) {
            throw new IllegalStateException("Xray authentication failed: HTTP " + response.statusCode()
                    + " - check XRAY_CLIENT_ID / XRAY_CLIENT_SECRET");
        }
        // WHY: Xray returns the token as a quoted JSON string, e.g. "eyJ...".
        return MAPPER.readValue(response.body(), String.class);
    }

    private HttpResponse<String> post(String path, String json, String bearerToken)
            throws IOException, InterruptedException {
        HttpRequest.Builder request = HttpRequest.newBuilder()
                .uri(URI.create(settings.normalizedBaseUrl() + path))
                .timeout(REQUEST_TIMEOUT)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json));
        if (bearerToken != null) {
            request.header("Authorization", "Bearer " + bearerToken);
        }
        return http.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }

    /** Cloud returns {"key": "QA-1"}; Data Center returns {"testExecIssue": {"key": "QA-1"}}. */
    static Optional<String> executionKey(String responseBody) throws IOException {
        JsonNode node = MAPPER.readTree(responseBody);
        if (node == null) {
            return Optional.empty();
        }
        JsonNode key = node.hasNonNull("key") ? node.get("key") : node.path("testExecIssue").get("key");
        return key == null || key.isNull() ? Optional.empty() : Optional.of(key.asText());
    }

    private void warnAboutUnlinkedScenarios(ArrayNode report) {
        String prefix = "@TEST_" + settings.projectKey() + "-";
        for (JsonNode feature : report) {
            for (JsonNode element : feature.path("elements")) {
                if (!"scenario".equals(element.path("type").asText())) {
                    continue;
                }
                boolean linked = false;
                for (JsonNode tag : element.path("tags")) {
                    linked |= tag.path("name").asText().startsWith(prefix);
                }
                if (!linked) {
                    log.debug("Scenario '{}' has no {}<n> tag; Xray will handle it per project settings",
                            element.path("name").asText(), prefix);
                }
            }
        }
    }
}
