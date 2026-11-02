package com.vanguard.core.context;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ScenarioContextTest {

    @Test
    public void storesAndReadsTypedValues() {
        ScenarioContext context = new ScenarioContext();
        context.set(ContextKey.SCENARIO_NAME, "login");

        assertThat(context.get(ContextKey.SCENARIO_NAME, String.class)).isEqualTo("login");
        assertThat(context.contains(ContextKey.SCENARIO_NAME)).isTrue();
    }

    @Test
    public void missingKeyExplainsWhatToDo() {
        ScenarioContext context = new ScenarioContext();
        context.set(ContextKey.SCENARIO_NAME, "x");

        assertThatThrownBy(() -> context.get(ContextKey.CREATED_USER, Object.class))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No value for ContextKey.CREATED_USER")
                .hasMessageContaining("Available keys: [SCENARIO_NAME]")
                .hasMessageContaining("JUNIOR_ONBOARDING_GUIDE");
    }

    @Test
    public void wrongTypeIsReported() {
        ScenarioContext context = new ScenarioContext();
        context.set(ContextKey.ORDER_TOTAL, "29.99");

        assertThatThrownBy(() -> context.get(ContextKey.ORDER_TOTAL, Integer.class))
                .hasMessageContaining("holds a String, not a Integer");
    }

    @Test
    public void resolvesRememberedPlaceholdersAndKeepsUnknownOnes() {
        ScenarioContext context = new ScenarioContext();
        context.remember("userId", 11);

        assertThat(context.resolve("/users/{userId}")).isEqualTo("/users/11");
        assertThat(context.resolve("{\"a\": {unknown}}")).isEqualTo("{\"a\": {unknown}}");
    }
}
