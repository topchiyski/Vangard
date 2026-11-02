package com.vanguard.api.steps;

import com.vanguard.api.client.GenericApiClient;
import com.vanguard.core.context.ContextKey;
import com.vanguard.core.context.ScenarioContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Ready-made API steps: write API tests WITHOUT Java. Every step is listed in docs/STEP_CATALOGUE.md.
 * Text parameters may contain {name} placeholders saved earlier with "I save the response field ... as ...".
 */
public class ApiCommonSteps {

    private final GenericApiClient client;
    private final ScenarioContext context;

    public ApiCommonSteps(GenericApiClient client, ScenarioContext context) {
        this.client = client;
        this.context = context;
    }

    @When("I send a {word} request to {string}")
    public void iSendARequestTo(String method, String path) {
        client.send(method, context.resolve(path), null);
    }

    @When("I send a {word} request to {string} with body:")
    public void iSendARequestWithBody(String method, String path, String body) {
        client.send(method, context.resolve(path), context.resolve(body));
    }

    @Then("the response status is {int}")
    public void theResponseStatusIs(int expected) {
        assertThat(lastResponse().statusCode())
                .as("HTTP status. Response body was: %s", lastResponse().asString())
                .isEqualTo(expected);
    }

    @Then("the response field {string} equals {string}")
    public void theResponseFieldEquals(String jsonPath, String expected) {
        assertThat(lastResponse().jsonPath().getString(jsonPath))
                .as("Response field '%s'", jsonPath)
                .isEqualTo(context.resolve(expected));
    }

    @Then("the response field {string} is not empty")
    public void theResponseFieldIsNotEmpty(String jsonPath) {
        assertThat(lastResponse().jsonPath().getString(jsonPath))
                .as("Response field '%s'", jsonPath)
                .isNotBlank();
    }

    @Then("the response list {string} has {int} items")
    public void theResponseListHasItems(String jsonPath, int expected) {
        assertThat(lastResponse().jsonPath().getList(jsonPath))
                .as("List at '%s'", jsonPath)
                .hasSize(expected);
    }

    @Then("the response matches the schema {string}")
    public void theResponseMatchesTheSchema(String schemaFile) {
        lastResponse().then().assertThat().body(matchesJsonSchemaInClasspath("schemas/" + schemaFile));
    }

    @Then("the response time is below {long} ms")
    public void theResponseTimeIsBelow(long maxMillis) {
        assertThat(lastResponse().time()).as("Response time in ms").isLessThan(maxMillis);
    }

    @Then("I save the response field {string} as {string}")
    public void iSaveTheResponseFieldAs(String jsonPath, String name) {
        String value = lastResponse().jsonPath().getString(jsonPath);
        assertThat(value).as("Response field '%s' to save as {%s}", jsonPath, name).isNotNull();
        context.remember(name, value);
    }

    private Response lastResponse() {
        return context.get(ContextKey.LAST_RESPONSE, Response.class);
    }
}
