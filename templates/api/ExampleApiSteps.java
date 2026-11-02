package com.vanguard.templates.api;

import com.vanguard.core.context.ContextKey;
import com.vanguard.core.context.ScenarioContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TEMPLATE: API Step Definitions.
 * Copy to vanguard-api/src/test/java/com/vanguard/api/steps/<Area>Steps.java and:
 * 1. TODO rename (e.g. OrderSteps); package com.vanguard.api.steps
 * 2. TODO inject your client + ScenarioContext in the constructor
 * Tip: for status codes, fields and schemas you can reuse the ready-made steps (docs/STEP_CATALOGUE.md).
 */
public class ExampleApiSteps {

    private final ExampleClient client;
    private final ScenarioContext context;

    public ExampleApiSteps(ExampleClient client, ScenarioContext context) {
        this.client = client;
        this.context = context;
    }

    @When("I create the todo {string}")
    public void iCreateTheTodo(String title) {
        client.createTodo(new ExampleDto(null, title, false));
    }

    @Then("the todo is created with the title {string}")
    public void theTodoIsCreated(String expectedTitle) {
        ExampleDto created = context.get(ContextKey.LAST_RESPONSE, Response.class)
                .then().statusCode(201)
                .extract().as(ExampleDto.class);
        assertThat(created.title()).isEqualTo(expectedTitle);
    }
}
