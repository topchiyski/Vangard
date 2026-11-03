package com.vanguard.mobile.steps;

import com.applitools.eyes.appium.Target;
import com.vanguard.mobile.visual.MobileEyesManager;
import io.cucumber.java.en.Then;

/**
 * Native visual checks (feature: mobile_visual.feature). Skipped with a warning when visual testing is off.
 */
public class MobileVisualSteps {

    @Then("the {string} screen matches its visual baseline")
    public void theScreenMatchesItsBaseline(String checkpoint) {
        MobileEyesManager.check(checkpoint, Target.window().strict());
    }
}
