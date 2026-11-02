package com.vanguard.core.report;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Merges Cucumber JSON reports and removes duplicate scenario runs caused by retries
 * (only the LAST attempt of each scenario is kept), so Xray sees one result per test.
 */
public final class CucumberJsonMerger {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Set<String> FAILED = Set.of("failed", "undefined", "pending", "ambiguous");

    /** Outcome of a single scenario after deduplication. */
    public enum Outcome { PASSED, FAILED, SKIPPED }

    private CucumberJsonMerger() {
    }

    /** Reads and merges several cucumber.json files, then deduplicates retried scenarios. */
    public static ArrayNode mergeFiles(List<Path> files) {
        ArrayNode all = MAPPER.createArrayNode();
        for (Path file : files) {
            try {
                JsonNode node = MAPPER.readTree(file.toFile());
                if (node != null && node.isArray()) {
                    all.addAll((ArrayNode) node);
                }
            } catch (IOException e) {
                throw new UncheckedIOException("Cannot read Cucumber report " + file, e);
            }
        }
        return deduplicate(all);
    }

    /** Parses a cucumber.json string and deduplicates retried scenarios. */
    public static ArrayNode deduplicate(String cucumberJson) {
        try {
            return deduplicate((ArrayNode) MAPPER.readTree(cucumberJson));
        } catch (IOException | ClassCastException e) {
            throw new IllegalArgumentException("Not a Cucumber JSON report (expected a JSON array)", e);
        }
    }

    /**
     * Groups features by URI and keeps only the last run of each scenario (identified by id + line).
     * Background elements stay attached to the scenario that follows them.
     */
    public static ArrayNode deduplicate(ArrayNode features) {
        Map<String, ObjectNode> byUri = new LinkedHashMap<>();
        Map<String, Map<String, List<JsonNode>>> scenariosByUri = new LinkedHashMap<>();

        for (JsonNode feature : features) {
            String uri = feature.path("uri").asText();
            byUri.computeIfAbsent(uri, u -> ((ObjectNode) feature).deepCopy());
            Map<String, List<JsonNode>> scenarios = scenariosByUri.computeIfAbsent(uri, u -> new LinkedHashMap<>());

            List<JsonNode> pendingBackground = new ArrayList<>();
            for (JsonNode element : feature.path("elements")) {
                if ("background".equals(element.path("type").asText())) {
                    pendingBackground.add(element);
                    continue;
                }
                List<JsonNode> group = new ArrayList<>(pendingBackground);
                group.add(element);
                pendingBackground.clear();
                // WHY: put() keeps the first position but stores the latest attempt.
                scenarios.put(element.path("id").asText() + ":" + element.path("line").asInt(), group);
            }
        }

        ArrayNode result = MAPPER.createArrayNode();
        byUri.forEach((uri, feature) -> {
            ArrayNode elements = MAPPER.createArrayNode();
            scenariosByUri.get(uri).values().forEach(elements::addAll);
            feature.set("elements", elements);
            result.add(feature);
        });
        return result;
    }

    /** Works out whether a scenario element passed, failed or was skipped. */
    public static Outcome outcomeOf(JsonNode scenario) {
        boolean anyPassed = false;
        for (String section : List.of("before", "steps", "after")) {
            for (JsonNode step : scenario.path(section)) {
                String status = step.path("result").path("status").asText();
                if (FAILED.contains(status)) {
                    return Outcome.FAILED;
                }
                anyPassed |= "passed".equals(status);
            }
        }
        return anyPassed ? Outcome.PASSED : Outcome.SKIPPED;
    }

    /** Serialises a report to pretty JSON. */
    public static String toJson(JsonNode node) {
        try {
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(node);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Writes a report to disk, creating parent folders. */
    public static void write(JsonNode node, Path target) {
        try {
            Files.createDirectories(target.getParent());
            Files.writeString(target, toJson(node));
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot write " + target, e);
        }
    }
}
