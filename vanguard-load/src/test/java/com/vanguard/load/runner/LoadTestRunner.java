package com.vanguard.load.runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/**
 * Runs load features ONE AT A TIME (load tests must not compete with each other)
 * and WITHOUT retries (a retried load test hides real problems).
 */
@CucumberOptions(
        features = "classpath:features",
        glue = {"com.vanguard.core", "com.vanguard.load.steps"},
        tags = "not @wip")
public class LoadTestRunner extends AbstractTestNGCucumberTests {
}
