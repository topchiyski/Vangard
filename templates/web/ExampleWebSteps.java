package com.vanguard.templates.web;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TEMPLATE: web Step Definitions.
 * Copy to vanguard-web/src/test/java/com/vanguard/web/steps/<Area>Steps.java and:
 * 1. TODO rename the class after the business area (e.g. ProfileSteps); package com.vanguard.web.steps
 * 2. TODO ask for your Page Objects in the constructor - Cucumber creates them (never use "new")
 * 3. TODO each step: 1-3 lines calling the Page Object, then an AssertJ assertion
 */
public class ExampleWebSteps {

    private final ExamplePage page;

    public ExampleWebSteps(ExamplePage page) {
        this.page = page;
    }

    @Given("I am on the TODO page")
    public void iAmOnTheTodoPage() {
        page.open();
    }

    @When("I save the name {string}")
    public void iSaveTheName(String name) {
        page.saveName(name);
    }

    @Then("I see the message {string}")
    public void iSeeTheMessage(String expected) {
        assertThat(page.successMessage()).isEqualTo(expected);
    }
}
