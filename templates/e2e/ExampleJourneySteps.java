package com.vanguard.templates.e2e;

import com.vanguard.api.client.UserClient;
import com.vanguard.api.dto.UserDto;
import com.vanguard.core.context.ContextKey;
import com.vanguard.core.context.ScenarioContext;
import com.vanguard.web.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TEMPLATE: cross-layer journey (API + web).
 * Copy to vanguard-e2e/src/test/java/com/vanguard/e2e/steps/<Journey>Steps.java and:
 * 1. TODO rename; package com.vanguard.e2e.steps
 * 2. TODO inject clients from vanguard-api and pages from vanguard-web - reuse, never copy
 * 3. TODO pass data between layers ONLY through ScenarioContext
 */
public class ExampleJourneySteps {

    private final UserClient users;
    private final LoginPage loginPage;
    private final ScenarioContext context;

    public ExampleJourneySteps(UserClient users, LoginPage loginPage, ScenarioContext context) {
        this.users = users;
        this.loginPage = loginPage;
        this.context = context;
    }

    @Given("a TODO user created through the API")
    public void aUserCreatedThroughTheApi() {
        UserDto created = users.createUser(UserDto.newUser("Journey User", "journey", "journey@example.com"))
                .then().statusCode(201).extract().as(UserDto.class);
        context.set(ContextKey.CREATED_USER, created);
    }

    @When("the TODO user opens the web app")
    public void theUserOpensTheWebApp() {
        loginPage.open();
    }

    @Then("the TODO web app shows the user's name")
    public void theWebAppShowsTheName() {
        UserDto user = context.get(ContextKey.CREATED_USER, UserDto.class);
        assertThat(user.name()).isNotBlank(); // TODO compare with what the page shows
    }
}
