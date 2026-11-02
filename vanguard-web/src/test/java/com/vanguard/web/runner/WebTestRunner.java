package com.vanguard.web.runner;

import com.vanguard.core.runner.ParallelCucumberRunner;
import io.cucumber.testng.CucumberOptions;

/** Runs every feature in src/test/resources/features. Filter with -Dcucumber.filter.tags="@smoke". */
@CucumberOptions(
        features = "classpath:features",
        glue = {"com.vanguard.core", "com.vanguard.web"},
        tags = "not @wip")
public class WebTestRunner extends ParallelCucumberRunner {
}
