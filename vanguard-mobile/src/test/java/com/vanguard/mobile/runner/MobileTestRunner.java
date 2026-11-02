package com.vanguard.mobile.runner;

import com.vanguard.core.runner.ParallelCucumberRunner;
import io.cucumber.testng.CucumberOptions;

/**
 * Runs mobile features (only with -Pmobile). Threads default to 1 for this module: one device, one scenario.
 * Note: the web module's hooks are NOT in the glue, so @mobileweb scenarios get Appium, not desktop Chrome.
 */
@CucumberOptions(
        features = "classpath:features",
        glue = {"com.vanguard.core", "com.vanguard.mobile"},
        tags = "not @wip")
public class MobileTestRunner extends ParallelCucumberRunner {
}
