package com.vanguard.load.steps;

import com.vanguard.core.config.ConfigProvider;
import com.vanguard.core.context.ContextKey;
import com.vanguard.core.context.ScenarioContext;
import com.vanguard.load.profile.LoadEndpoints;
import com.vanguard.load.profile.LoadProfile;
import com.vanguard.load.runner.GatlingRunner;
import com.vanguard.load.runner.LoadResult;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Every load-test step. Juniors write load tests in Gherkin only; see docs/STEP_CATALOGUE.md.
 *
 * <pre>
 * Given a load profile "browse" against the "products" endpoint:
 *   | users | rampUpSeconds | durationSeconds |
 *   | 50    | 30            | 120             |
 * When I run the load test
 * Then the error rate is below 1 percent
 * And the 95th percentile response time is below 500 ms
 * </pre>
 */
@Slf4j
public class LoadSteps {

    /** Used by "When I run the load test": users send GET requests to the profile's endpoint. */
    private static final String DEFAULT_SIMULATION = "ApiBrowseSimulation";

    private final ScenarioContext context;
    private final GatlingRunner runner;
    private Scenario scenario;

    public LoadSteps(ScenarioContext context, GatlingRunner runner) {
        this.context = context;
        this.runner = runner;
    }

    @Before("@load")
    public void rememberScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    @Given("a load profile {string} against the {string} endpoint:")
    public void aLoadProfile(String name, String endpoint, DataTable table) {
        // WHY: fails here, in seconds, instead of deep inside the separate Gatling JVM.
        LoadEndpoints.pathFor(endpoint);
        Map<String, String> row = table.asMaps().get(0);
        LoadProfile requested = LoadProfile.fromTableRow(name, endpoint, row);
        LoadProfile effective = requested.limitedTo(ConfigProvider.get().loadProfile());
        if (!effective.equals(requested)) {
            log.info("Smoke load profile: capped to {} users / {}s (run with -Dload.profile=full for the real load)",
                    effective.users(), effective.durationSeconds());
        }
        context.set(ContextKey.LOAD_PROFILE, effective);
    }

    @When("I run the load test")
    public void iRunTheLoadTest() {
        iRunTheLoadTest(DEFAULT_SIMULATION);
    }

    @When("I run the {string} load test")
    public void iRunTheLoadTest(String simulation) {
        LoadResult result = runner.run(simulation, context.get(ContextKey.LOAD_PROFILE, LoadProfile.class));
        context.set(ContextKey.LOAD_RESULT, result);
        attachToReport(result);
    }

    @Then("the error rate is below {double} percent")
    public void theErrorRateIsBelow(double maxPercent) {
        assertThat(result().errorRatePercent()).as("error rate in %").isLessThan(maxPercent);
    }

    @Then("the {int}th percentile response time is below {long} ms")
    public void thePercentileIsBelow(int percentile, long maxMs) {
        assertThat(result().percentileMs(percentile)).as("p%d response time in ms", percentile).isLessThan(maxMs);
    }

    @Then("the mean response time is below {long} ms")
    public void theMeanIsBelow(long maxMs) {
        assertThat(result().meanMs()).as("mean response time in ms").isLessThan(maxMs);
    }

    @Then("the throughput is at least {double} requests per second")
    public void theThroughputIsAtLeast(double minRps) {
        assertThat(result().requestsPerSecond()).as("throughput in requests/second").isGreaterThanOrEqualTo(minRps);
    }

    @Then("at least {long} requests were sent")
    public void atLeastRequestsWereSent(long minRequests) {
        assertThat(result().totalRequests()).as("number of requests").isGreaterThanOrEqualTo(minRequests);
    }

    private LoadResult result() {
        return context.get(ContextKey.LOAD_RESULT, LoadResult.class);
    }

    private void attachToReport(LoadResult result) {
        if (scenario == null) {
            return;
        }
        scenario.attach(result.toHtmlTable(), "text/html", "Load test results");
        try {
            scenario.attach(Files.readString(result.consoleLog()), "text/plain", "Gatling console");
            Path index = result.reportDir() == null ? null : result.reportDir().resolve("index.html");
            if (index != null && Files.exists(index)) {
                scenario.attach(index.toUri().toString(), "text/uri-list", "Full Gatling HTML report");
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
