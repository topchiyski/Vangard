package com.vanguard.templates.mobile;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TEMPLATE: mobile Step Definitions.
 * Copy to vanguard-mobile/src/test/java/com/vanguard/mobile/steps/<Area>Steps.java and:
 * 1. TODO rename; package com.vanguard.mobile.steps
 * 2. TODO inject Screen Objects in the constructor (never "new")
 * The same steps run on Android and iOS.
 */
public class ExampleMobileSteps {

    private final ExampleScreen screen;

    public ExampleMobileSteps(ExampleScreen screen) {
        this.screen = screen;
    }

    @When("I save my TODO settings")
    public void iSaveMySettings() {
        screen.save();
    }

    @Then("the TODO screen title is {string}")
    public void theScreenTitleIs(String expected) {
        assertThat(screen.title()).isEqualTo(expected);
    }
}
