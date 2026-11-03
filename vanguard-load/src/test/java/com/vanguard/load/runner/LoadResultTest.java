package com.vanguard.load.runner;

import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * The fixture is real Gatling 3.16 console output. After a Gatling upgrade, replace it with a fresh
 * gatling-console.log from target/gatling/ and run this test.
 */
public class LoadResultTest {

    @Test
    public void readsTheGlobalInformationSummary() throws Exception {
        LoadResult result = LoadResult.fromReport(null, fixture());

        assertThat(result.totalRequests()).isEqualTo(401);
        assertThat(result.failedRequests()).as("KO column shows '-' when nothing failed").isZero();
        assertThat(result.meanMs()).isEqualTo(15);
        assertThat(result.percentileMs(50)).isEqualTo(14);
        assertThat(result.percentileMs(95)).isEqualTo(30);
        assertThat(result.percentileMs(99)).isEqualTo(34);
        assertThat(result.maxMs()).isEqualTo(37);
        assertThat(result.requestsPerSecond()).isEqualTo(19.1);
    }

    @Test
    public void computesTheErrorRateFromTheKoColumn() throws Exception {
        Path log = variant("|       401 |       401 |         -", "|       400 |       396 |         4");

        LoadResult result = LoadResult.fromReport(null, log);

        assertThat(result.failedRequests()).isEqualTo(4);
        assertThat(result.errorRatePercent()).isEqualTo(1.0);
    }

    @Test
    public void aMissingRowFailsInsteadOfReadingZero() throws Exception {
        Path log = variant("response time 95th percentile (ms)", "response time p95 (ms)");

        assertThatThrownBy(() -> LoadResult.fromReport(null, log))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("summary format changed")
                .hasMessageContaining("response time 95th percentile (ms)");
    }

    @Test
    public void unreadableSummaryRowsSayTheFormatChanged() throws Exception {
        Path log = variant("> ", "* ");

        assertThatThrownBy(() -> LoadResult.fromReport(null, log))
                .hasMessageContaining("summary format changed");
    }

    @Test
    public void noSummaryAtAllAsksWhetherRequestsRan() throws Exception {
        Path log = Files.createTempFile("gatling", ".log");
        Files.writeString(log, "Simulation failed to start\n");

        assertThatThrownBy(() -> LoadResult.fromReport(null, log))
                .hasMessageContaining("did any request run?");
    }

    @Test
    public void onlyGatlingPercentilesAreSupported() throws Exception {
        LoadResult result = LoadResult.fromReport(null, fixture());

        assertThatThrownBy(() -> result.percentileMs(90))
                .hasMessageContaining("Supported percentiles: 50, 95, 99");
    }

    private static Path fixture() throws URISyntaxException {
        return Path.of(Objects.requireNonNull(
                LoadResultTest.class.getResource("/gatling/console-summary-3.16.txt")).toURI());
    }

    /**
     * The fixture with every {@code from} replaced by {@code to}.
     */
    private static Path variant(String from, String to) throws IOException, URISyntaxException {
        Path log = Files.createTempFile("gatling", ".log");
        return Files.writeString(log, Files.readString(fixture()).replace(from, to));
    }
}
