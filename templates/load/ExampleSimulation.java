package com.vanguard.templates.load;

import com.vanguard.load.simulation.ProfiledSimulation;
import io.gatling.javaapi.core.ScenarioBuilder;

import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

/**
 * TEMPLATE: Gatling simulation (Java DSL - no Scala).
 * Often you do NOT need a new simulation: "When I run the load test" works for any GET endpoint.
 * Copy to vanguard-load/src/main/java/com/vanguard/load/simulation/<Name>Simulation.java and:
 * 1. TODO rename (e.g. CheckoutSimulation); package com.vanguard.load.simulation
 * 2. TODO describe ONE user's journey in the scenario: requests + checks + think time
 * 3. Keep setUp(...) as is: users, ramp-up and duration come from the Gherkin table.
 */
public class ExampleSimulation extends ProfiledSimulation {

    private final ScenarioBuilder journey = scenario(profile().name())
            .exec(http("TODO list items").get("/TODO-items").check(status().is(200)))
            .pause(thinkTime())
            .exec(http("TODO open one item").get("/TODO-items/1").check(status().is(200)))
            .pause(thinkTime());

    {
        setUp(journey.injectClosed(loadShape()).protocols(httpProtocol()));
    }
}
