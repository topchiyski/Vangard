package com.vanguard.load.simulation;

import io.gatling.javaapi.core.ScenarioBuilder;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static io.gatling.javaapi.core.CoreDsl.feed;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

/**
 * REFERENCE EXAMPLE of a simulation: users read random products from the profile's endpoint.
 * Copy templates/load/ExampleSimulation.java to create your own.
 */
public class ApiBrowseSimulation extends ProfiledSimulation {

    /**
     * Endless supply of random product ids, used as #{productId} in the path.
     */
    private final Iterator<Map<String, Object>> productIds = Stream.generate(
            () -> Map.<String, Object>of("productId", ThreadLocalRandom.current().nextInt(1, 101))).iterator();

    private final ScenarioBuilder browse = scenario(profile().name())
            .exec(feed(productIds))
            .exec(http("GET " + profile().endpoint()).get(path()).check(status().is(200)))
            .pause(thinkTime());

    {
        setUp(browse.injectClosed(loadShape()).protocols(httpProtocol()));
    }
}
