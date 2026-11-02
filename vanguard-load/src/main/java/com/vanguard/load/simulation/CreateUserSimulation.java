package com.vanguard.load.simulation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vanguard.api.dto.UserDto;
import io.gatling.javaapi.core.ScenarioBuilder;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

/**
 * Example of a POST load test that REUSES the API module's record DTO ({@link UserDto}) for the body,
 * so API tests and load tests always send the same JSON shape.
 */
public class CreateUserSimulation extends ProfiledSimulation {

    private static final String BODY = toJson(UserDto.newUser("Load User", "load.user", "load@example.com"));

    private final ScenarioBuilder create = scenario(profile().name())
            .exec(http("POST /users").post("/users").body(StringBody(BODY))
                    .check(status().is(201), jsonPath("$.id").exists()))
            .pause(thinkTime());

    {
        setUp(create.injectClosed(loadShape()).protocols(httpProtocol()));
    }

    private static String toJson(Object body) {
        try {
            return new ObjectMapper().writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }
}
