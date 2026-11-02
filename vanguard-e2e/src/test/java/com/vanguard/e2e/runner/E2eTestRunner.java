package com.vanguard.e2e.runner;

import com.vanguard.core.runner.ParallelCucumberRunner;
import io.cucumber.testng.CucumberOptions;

/**
 * Glue = core + the ready-made steps and hooks of the modules this journey uses + E2E's own steps.
 * Add "com.vanguard.mobile.hooks" / "com.vanguard.mobile.steps" here for journeys that use a phone.
 */
@CucumberOptions(
        features = "classpath:features",
        glue = {"com.vanguard.core", "com.vanguard.api.steps", "com.vanguard.web.hooks",
                "com.vanguard.web.steps", "com.vanguard.e2e"},
        tags = "not @wip")
public class E2eTestRunner extends ParallelCucumberRunner {
}
