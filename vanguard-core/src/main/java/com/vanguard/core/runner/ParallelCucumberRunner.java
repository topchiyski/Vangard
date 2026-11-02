package com.vanguard.core.runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import org.testng.annotations.DataProvider;

/**
 * Base class for module runners that execute scenarios in parallel.
 * The thread count comes from {@code data-provider-thread-count} in the module's testng.xml ({@code -Dthreads=N}).
 *
 * <p>Each module has exactly one runner, e.g.:
 * <pre>{@code
 * @CucumberOptions(features = "classpath:features", glue = {"com.vanguard.core", "com.vanguard.api.steps"})
 * public class ApiTestRunner extends ParallelCucumberRunner { }
 * }</pre>
 */
public abstract class ParallelCucumberRunner extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
