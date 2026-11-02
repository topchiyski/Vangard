package com.vanguard.load.simulation;

import com.vanguard.load.profile.LoadProfile;
import io.gatling.javaapi.core.ClosedInjectionStep;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.constantConcurrentUsers;
import static io.gatling.javaapi.core.CoreDsl.rampConcurrentUsers;
import static io.gatling.javaapi.http.HttpDsl.http;

/**
 * Parent of every Vanguard simulation (Gatling Java DSL - no Scala).
 * It reads the {@link LoadProfile} from the Gherkin table and offers ready-made building blocks:
 *
 * <pre>{@code
 * public class HealthCheckSimulation extends ProfiledSimulation {
 *     {
 *         ScenarioBuilder check = scenario("Health").exec(http("GET health").get(path()).check(status().is(200)))
 *                 .pause(thinkTime());
 *         setUp(check.injectClosed(loadShape()).protocols(httpProtocol()));
 *     }
 * }
 * }</pre>
 */
public abstract class ProfiledSimulation extends Simulation {

    /** System property with the target base URL (WireMock by default), set by GatlingRunner. */
    public static final String BASE_URL_PROPERTY = "vanguard.load.baseUrl";

    private final LoadProfile profile = LoadProfile.fromSystemProperty();

    /** The load profile from the feature file. */
    protected LoadProfile profile() {
        return profile;
    }

    /** HTTP settings shared by all requests: base URL and JSON headers. */
    protected HttpProtocolBuilder httpProtocol() {
        return http.baseUrl(System.getProperty(BASE_URL_PROPERTY))
                .acceptHeader("application/json")
                .contentTypeHeader("application/json")
                .userAgentHeader("Vanguard-Load");
    }

    /**
     * "users" concurrent users: ramp up from 0, then hold for the duration. (Closed model: when a user
     * finishes, a new one starts, so concurrency stays constant.)
     */
    protected ClosedInjectionStep[] loadShape() {
        return new ClosedInjectionStep[]{
                rampConcurrentUsers(0).to(profile.users()).during(Duration.ofSeconds(Math.max(1, profile.rampUpSeconds()))),
                constantConcurrentUsers(profile.users()).during(Duration.ofSeconds(profile.durationSeconds()))
        };
    }

    /** The request path for the profile's endpoint. */
    protected String path() {
        return com.vanguard.load.profile.LoadEndpoints.pathFor(profile.endpoint());
    }

    /** Pause between a user's requests. */
    protected Duration thinkTime() {
        return Duration.ofMillis(profile.thinkTimeMs());
    }
}
