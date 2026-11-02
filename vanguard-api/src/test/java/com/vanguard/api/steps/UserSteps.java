package com.vanguard.api.steps;

import com.vanguard.api.client.UserClient;
import com.vanguard.api.dto.UserDto;
import com.vanguard.core.context.ContextKey;
import com.vanguard.core.context.ScenarioContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * REFERENCE EXAMPLE of API step definitions (feature: user_crud_and_schema.feature).
 *
 * <p>Pattern: steps speak business language and delegate HTTP details to {@link UserClient}.
 * The client stores every response in {@link ScenarioContext}, so Then-steps simply read it back.
 */
public class UserSteps {

    private final UserClient users;
    private final ScenarioContext context;

    // WHY: Cucumber (PicoContainer) creates UserClient and ScenarioContext for us. Never use "new" here.
    public UserSteps(UserClient users, ScenarioContext context) {
        this.users = users;
        this.context = context;
    }

    @When("I request the user with id {int}")
    public void iRequestTheUserWithId(int id) {
        users.getUser(id);
    }

    @When("I create a user named {string} with email {string}")
    public void iCreateAUser(String name, String email) {
        UserDto newUser = UserDto.newUser(name, name.toLowerCase().replace(' ', '.'), email);
        UserDto created = users.createUser(newUser)
                .then().statusCode(201)
                .extract().as(UserDto.class);
        context.set(ContextKey.CREATED_USER, created);
    }

    @When("I try to create a user named {string} without an email")
    public void iTryToCreateAUserWithoutEmail(String name) {
        users.createUser(UserDto.newUser(name, "no.email", null));
    }

    @When("I rename the user with id {int} to {string}")
    public void iRenameTheUser(int id, String newName) {
        users.updateUser(id, new UserDto(id, newName, "renamed", "renamed@example.com"));
    }

    @When("I delete the user with id {int}")
    public void iDeleteTheUser(int id) {
        users.deleteUser(id);
    }

    @Then("the user is returned with name {string}")
    public void theUserIsReturnedWithName(String expectedName) {
        UserDto user = lastResponse().then().statusCode(200).extract().as(UserDto.class);
        assertThat(user.name()).isEqualTo(expectedName);
    }

    @Then("the user matches the user schema")
    public void theUserMatchesTheUserSchema() {
        lastResponse().then().body(matchesJsonSchemaInClasspath("schemas/user-schema.json"));
    }

    @Then("the user is created with a new id")
    public void theUserIsCreatedWithANewId() {
        UserDto created = context.get(ContextKey.CREATED_USER, UserDto.class);
        assertThat(created.id()).as("id assigned by the server").isNotNull().isPositive();
    }

    @Then("the created user has the email {string}")
    public void theCreatedUserHasTheEmail(String expectedEmail) {
        assertThat(context.get(ContextKey.CREATED_USER, UserDto.class).email()).isEqualTo(expectedEmail);
    }

    @Then("the user is deleted")
    public void theUserIsDeleted() {
        assertThat(lastResponse().statusCode()).as("HTTP status for DELETE").isEqualTo(204);
    }

    private Response lastResponse() {
        return context.get(ContextKey.LAST_RESPONSE, Response.class);
    }
}
