package com.vanguard.core.report;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CucumberJsonMergerTest {

    /**
     * A feature where scenario "login" ran twice (failed, then passed on retry).
     */
    public static final String RETRIED_REPORT = """
            [ { "uri": "classpath:features/login.feature", "name": "Login", "elements": [
                { "type": "background", "name": "bg", "steps": [ { "result": { "status": "passed" } } ] },
                { "type": "scenario", "id": "login;ok", "line": 5, "name": "ok",
                  "tags": [ { "name": "@TEST_QA-1" } ],
                  "steps": [ { "result": { "status": "failed" } } ] },
                { "type": "background", "name": "bg", "steps": [ { "result": { "status": "passed" } } ] },
                { "type": "scenario", "id": "login;ok", "line": 5, "name": "ok",
                  "tags": [ { "name": "@TEST_QA-1" } ],
                  "steps": [ { "result": { "status": "passed" } } ] }
            ] } ]
            """;

    @Test
    public void keepsOnlyTheLastAttemptOfARetriedScenario() {
        ArrayNode result = CucumberJsonMerger.deduplicate(RETRIED_REPORT);

        JsonNode elements = result.get(0).get("elements");
        assertThat(elements).hasSize(2); // background + last scenario attempt
        assertThat(elements.get(0).get("type").asText()).isEqualTo("background");
        assertThat(CucumberJsonMerger.outcomeOf(elements.get(1))).isEqualTo(CucumberJsonMerger.Outcome.PASSED);
    }

    @Test
    public void detectsFailedAndSkippedScenarios() {
        ArrayNode report = CucumberJsonMerger.deduplicate("""
                [ { "uri": "a", "elements": [
                  { "type": "scenario", "id": "a;1", "line": 1, "steps": [ { "result": { "status": "undefined" } } ] },
                  { "type": "scenario", "id": "a;2", "line": 2, "steps": [ { "result": { "status": "skipped" } } ] }
                ] } ]""");

        JsonNode elements = report.get(0).get("elements");
        assertThat(CucumberJsonMerger.outcomeOf(elements.get(0))).isEqualTo(CucumberJsonMerger.Outcome.FAILED);
        assertThat(CucumberJsonMerger.outcomeOf(elements.get(1))).isEqualTo(CucumberJsonMerger.Outcome.SKIPPED);
    }
}
