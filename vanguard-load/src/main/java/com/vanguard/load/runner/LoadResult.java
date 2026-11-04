package com.vanguard.load.runner;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The numbers of a finished load test, used by the SLA steps ("Then the error rate is below 1 percent").
 *
 * @param totalRequests   all requests sent
 * @param failedRequests  requests that failed a check (Gatling "KO")
 * @param meanMs          mean response time
 * @param p50Ms           median response time
 * @param p95Ms           95th percentile response time
 * @param p99Ms           99th percentile response time
 * @param maxMs           slowest response
 * @param requestsPerSecond mean throughput
 * @param reportDir       folder with Gatling's HTML report (open index.html)
 * @param consoleLog      Gatling's console output
 */
public record LoadResult(long totalRequests, long failedRequests, long meanMs, long p50Ms, long p95Ms,
                         long p99Ms, long maxMs, double requestsPerSecond, Path reportDir, Path consoleLog) {

    // "> request count     |     97 |     97 |      -"
    private static final Pattern ROW = Pattern.compile("^> (.+?)\\s*\\|\\s*([\\d.]+|-)\\s*\\|\\s*([\\d.]+|-)\\s*\\|\\s*([\\d.]+|-)\\s*$");

    private static final String FORMAT_CHANGED = "Gatling's console summary format changed: update LoadResult "
            + "(see vanguard-load/CLAUDE.md) - ";

    /**
     * Percentage of failed requests, 0-100.
     *
     * @throws IllegalStateException when the load run sent no requests
     */
    public double errorRatePercent() {
        if (totalRequests == 0) {
            throw new IllegalStateException("Cannot calculate error rate: the load run sent no requests.");
        }
        return failedRequests * 100.0 / totalRequests;
    }

    /**
     * Response time for a percentile supported by Gatling's summary (50, 95 or 99).
     */
    public long percentileMs(int percentile) {
        return switch (percentile) {
            case 50 -> p50Ms;
            case 95 -> p95Ms;
            case 99 -> p99Ms;
            default -> throw new IllegalArgumentException("Supported percentiles: 50, 95, 99 (got " + percentile + ")");
        };
    }

    /**
     * A small HTML table for the Allure report.
     */
    public String toHtmlTable() {
        return """
                <table border="1" cellpadding="4">
                <tr><th>Requests</th><th>Failed</th><th>Error rate</th><th>Mean</th><th>p50</th><th>p95</th><th>p99</th><th>Max</th><th>Throughput</th></tr>
                <tr><td>%d</td><td>%d</td><td>%.2f %%</td><td>%d ms</td><td>%d ms</td><td>%d ms</td><td>%d ms</td><td>%d ms</td><td>%.2f req/s</td></tr>
                </table>
                """.formatted(totalRequests, failedRequests, errorRatePercent(), meanMs, p50Ms, p95Ms, p99Ms, maxMs,
                requestsPerSecond);
    }

    /**
     * Reads the "Global Information" summary Gatling prints at the end of a run.
     * Verified against Gatling 3.16 (which no longer writes js/stats.json).
     */
    static LoadResult fromReport(Path reportDir, Path consoleLog) {
        Map<String, String> totals = new HashMap<>();
        Map<String, String> failed = new HashMap<>();
        boolean sawSummary = false;
        try {
            List<String> lines = Files.readAllLines(consoleLog);
            for (String line : lines) {
                sawSummary |= line.contains("Global Information");
                Matcher m = ROW.matcher(line.trim());
                if (m.matches()) {
                    totals.putIfAbsent(m.group(1).trim(), m.group(2));
                    failed.putIfAbsent(m.group(1).trim(), m.group(4));
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read Gatling output " + consoleLog, e);
        }
        if (totals.isEmpty()) {
            throw new IllegalStateException(sawSummary
                    ? FORMAT_CHANGED + "no summary row could be read. See " + consoleLog
                    : "Gatling produced no statistics - did any request run? See " + consoleLog);
        }
        return new LoadResult(
                number(totals, "request count", consoleLog),
                number(failed, "request count", consoleLog),
                number(totals, "mean response time (ms)", consoleLog),
                number(totals, "response time 50th percentile (ms)", consoleLog),
                number(totals, "response time 95th percentile (ms)", consoleLog),
                number(totals, "response time 99th percentile (ms)", consoleLog),
                number(totals, "max response time (ms)", consoleLog),
                decimal(totals, "mean throughput (rps)", consoleLog),
                reportDir, consoleLog);
    }

    private static long number(Map<String, String> values, String label, Path consoleLog) {
        return Math.round(decimal(values, label, consoleLog));
    }

    // WHY: a missing row must fail - reading it as 0 would make "below 500 ms" checks pass silently.
    private static double decimal(Map<String, String> values, String label, Path consoleLog) {
        String value = values.get(label);
        if (value == null) {
            throw new IllegalStateException(FORMAT_CHANGED + "no row \"" + label + "\". See " + consoleLog);
        }
        // Gatling prints "-" for an empty column, e.g. when no request failed.
        return "-".equals(value) ? 0 : Double.parseDouble(value);
    }
}
