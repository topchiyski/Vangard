package com.vanguard.core.steps;

import com.vanguard.core.context.ScenarioContext;
import io.cucumber.java.en.Given;

/**
 * Common steps available in every module. Documented in docs/STEP_CATALOGUE.md.
 */
public class ContextSteps {

    private final ScenarioContext context;

    public ContextSteps(ScenarioContext context) {
        this.context = context;
    }

    /**
     * Example: {@code Given I remember "Sauce Labs Backpack" as "product"} then use {@code {product}} later.
     */
    @Given("I remember {string} as {string}")
    public void iRemember(String value, String name) {
        context.remember(name, context.resolve(value));
    }
}
