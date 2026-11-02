package com.vanguard.web.steps;

import com.applitools.eyes.selenium.fluent.Target;
import com.vanguard.web.pages.InventoryPage;
import com.vanguard.web.visual.WebEyesManager;
import io.cucumber.java.en.Then;

/**
 * Visual checks with Applitools (feature: web_visual.feature). Skipped with a warning when visual
 * testing is off, so they never break a local run without an API key.
 */
public class VisualSteps {

    @Then("the {string} page matches its visual baseline")
    public void thePageMatchesItsBaseline(String checkpoint) {
        WebEyesManager.check(checkpoint, Target.window().fully().strict());
    }

    @Then("the {string} page matches its visual baseline ignoring prices")
    public void thePageMatchesItsBaselineIgnoringPrices(String checkpoint) {
        WebEyesManager.check(checkpoint, Target.window().fully().ignore(InventoryPage.PRICES));
    }

    @Then("the {string} page layout matches its visual baseline")
    public void thePageLayoutMatchesItsBaseline(String checkpoint) {
        WebEyesManager.check(checkpoint, Target.window().fully().layout());
    }
}
