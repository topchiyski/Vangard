package com.vanguard.reporting;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.vanguard.core.config.ConfigProvider;
import com.vanguard.core.report.CucumberJsonMerger;
import com.vanguard.core.report.CucumberJsonMerger.Outcome;
import com.vanguard.core.xray.XrayResultsExporter;
import com.vanguard.core.xray.XraySettings;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

/**
 * Runs LAST in the build. It:
 * <ol>
 *   <li>collects every module's {@code target/allure-results} into {@code <root>/target/allure-results},</li>
 *   <li>writes Allure's {@code environment.properties},</li>
 *   <li>merges every {@code cucumber.json} into {@code <root>/target/cucumber-reports/cucumber.json}
 *       (keeping only the last attempt of retried scenarios),</li>
 *   <li>uploads the merged report to Xray when {@code -Dxray.upload.enabled=true},</li>
 *   <li>prints a summary and FAILS the build if any scenario failed.</li>
 * </ol>
 *
 * <p>Usage: {@code ReportAggregator --root <repo> [--since <ISO instant>]} (Maven, local)
 * or {@code ReportAggregator --input <downloaded-artifacts-dir> --root <repo>} (CI).
 */
@Slf4j
public final class ReportAggregator {

    private static final String CUCUMBER_JSON = "cucumber.json";
    private static final String ALLURE_RESULTS = "allure-results";

    private final Path root;
    private final Path input;
    private final Instant since;

    ReportAggregator(Path root, Path input, Instant since) {
        this.root = root.toAbsolutePath().normalize();
        this.input = input == null ? this.root : input.toAbsolutePath().normalize();
        this.since = since;
    }

    /**
     * Command-line entry point (CI): {@code --root <dir> [--input <dir>] [--since <ISO instant>]}.
     */
    public static void main(String[] args) {
        Path root = Path.of(".");
        Path input = null;
        Instant since = null;
        for (int i = 0; i + 1 < args.length; i += 2) {
            switch (args[i]) {
                case "--root" -> root = Path.of(args[i + 1]);
                case "--input" -> input = Path.of(args[i + 1]);
                case "--since" -> since = Instant.parse(args[i + 1]);
                default -> throw new IllegalArgumentException("Unknown argument " + args[i]
                        + ". Use --root <dir> [--input <dir>] [--since <ISO instant>]");
            }
        }
        new ReportAggregator(root, input, since).run();
    }

    void run() {
        Path outDir = root.resolve("target");
        List<Path> reports = findCucumberReports(outDir);
        if (reports.isEmpty()) {
            log.warn("No cucumber.json found under {} - nothing to aggregate. Did any test module run?", input);
            return;
        }

        copyAllureResults(reports, outDir.resolve(ALLURE_RESULTS));
        EnvironmentWriter.write(outDir.resolve(ALLURE_RESULTS).resolve("environment.properties"));

        ArrayNode merged = CucumberJsonMerger.mergeFiles(reports);
        Path mergedFile = outDir.resolve("cucumber-reports").resolve(CUCUMBER_JSON);
        CucumberJsonMerger.write(merged, mergedFile);
        log.info("Merged {} report(s) into {}", reports.size(), mergedFile);

        if (ConfigProvider.get().xrayUploadEnabled()) {
            new XrayResultsExporter(XraySettings.fromConfig()).upload(CucumberJsonMerger.toJson(merged));
        }

        int failed = printSummary(reports);
        if (failed > 0) {
            throw new IllegalStateException(failed + " scenario(s) failed. Open the Allure report: mvn -N allure:serve");
        }
    }

    /**
     * Every module's target/cucumber-reports/cucumber.json (fresh ones only when --since is given).
     */
    private List<Path> findCucumberReports(Path ownOutput) {
        try (Stream<Path> files = Files.walk(input)) {
            return files
                    .filter(p -> p.getFileName().toString().equals(CUCUMBER_JSON))
                    .filter(p -> !p.startsWith(ownOutput))
                    .filter(p -> !p.toString().contains("node_modules"))
                    .filter(this::isFresh)
                    .sorted()
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private boolean isFresh(Path file) {
        if (since == null) {
            return true;
        }
        try {
            // WHY: no tolerance - the build timestamp is truncated to the second, so this run's files are never
            // older than it, while a tolerance let results of an immediately preceding run leak in.
            return !Files.getLastModifiedTime(file).toInstant().isBefore(since);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * allure-results sits next to cucumber-reports in each module's target folder.
     */
    private void copyAllureResults(List<Path> reports, Path target) {
        try {
            if (Files.exists(target)) {
                try (Stream<Path> old = Files.list(target)) {
                    for (Path p : old.toList()) {
                        Files.deleteIfExists(p);
                    }
                }
            }
            Files.createDirectories(target);
            for (Path report : reports) {
                Path results = report.getParent().getParent().resolve(ALLURE_RESULTS);
                if (!Files.isDirectory(results)) {
                    continue;
                }
                try (Stream<Path> files = Files.list(results)) {
                    for (Path file : files.filter(Files::isRegularFile).toList()) {
                        Files.copy(file, target.resolve(file.getFileName()),
                                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot collect Allure results", e);
        }
    }

    /**
     * Prints passed/failed/skipped per module and returns the number of failed scenarios.
     */
    private int printSummary(List<Path> reports) {
        Map<String, Map<Outcome, Integer>> perModule = new TreeMap<>();
        List<String> failures = new ArrayList<>();
        for (Path report : reports) {
            String module = moduleName(report);
            Map<Outcome, Integer> counts = perModule.computeIfAbsent(module, m -> new EnumMap<>(Outcome.class));
            for (JsonNode feature : CucumberJsonMerger.mergeFiles(List.of(report))) {
                for (JsonNode element : feature.path("elements")) {
                    if ("background".equals(element.path("type").asText())) {
                        continue;
                    }
                    Outcome outcome = CucumberJsonMerger.outcomeOf(element);
                    counts.merge(outcome, 1, Integer::sum);
                    if (outcome == Outcome.FAILED) {
                        failures.add(module + " :: " + element.path("name").asText()
                                + " (" + feature.path("uri").asText() + ")");
                    }
                }
            }
        }

        StringBuilder table = new StringBuilder(System.lineSeparator())
                .append("==================== VANGUARD RESULTS ====================").append(System.lineSeparator())
                .append(String.format("%-22s %8s %8s %8s%n", "Module", "Passed", "Failed", "Skipped"));
        int failed = 0;
        for (var entry : perModule.entrySet()) {
            Map<Outcome, Integer> c = entry.getValue();
            failed += c.getOrDefault(Outcome.FAILED, 0);
            table.append(String.format("%-22s %8d %8d %8d%n", entry.getKey(),
                    c.getOrDefault(Outcome.PASSED, 0), c.getOrDefault(Outcome.FAILED, 0),
                    c.getOrDefault(Outcome.SKIPPED, 0)));
        }
        failures.forEach(f -> table.append("  FAILED: ").append(f).append(System.lineSeparator()));
        table.append("==========================================================");
        log.info(table.toString());
        return failed;
    }

    /**
     * vanguard-api/target/cucumber-reports/cucumber.json -> vanguard-api.
     * In CI, artifacts may be flattened (results-load/cucumber-reports/cucumber.json) -> results-load.
     */
    private static String moduleName(Path report) {
        for (Path dir = report.getParent(); dir != null && dir.getFileName() != null; dir = dir.getParent()) {
            String name = dir.getFileName().toString();
            if (name.startsWith("vanguard-") || name.startsWith("results-")) {
                return name;
            }
        }
        return report.getParent().getParent().getFileName().toString();
    }
}
